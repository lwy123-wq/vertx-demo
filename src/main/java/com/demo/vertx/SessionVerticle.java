package com.demo.vertx;

import com.demo.service.MyDb;
import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;

public class SessionVerticle extends AbstractVerticle {
    MySQLConnectOptions connection=new MySQLConnectOptions()
            .setPort(3306)
            .setHost("localhost")
            .setDatabase("vertx")
            .setUser("root")
            .setPassword("lwy0328");
    PoolOptions poolOptions=new PoolOptions()
            .setMaxSize(5);
    MySQLPool client;
    Router router;
    public void start()throws Exception{

        router=Router.router(vertx);
        client=MySQLPool.pool(vertx,connection,poolOptions);
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        AuthenticationProvider authenticationProvider=new AuthenticationProvider() {
            @Override
            public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {
                String username=jsonObject.getString("username");
                String password=jsonObject.getString("password");
                System.out.println("username:"+username+"password:"+password);
            }
        };
        /*router.post("/login").handler(FormLoginHandler.create(authenticationProvider));
        router.route().handler(StaticHandler.create());*/
        router.route("/login").handler(context->{
            MultiMap queryParams = context.queryParams();
            String username = queryParams.get("username");
            String password = queryParams.get("password");
            System.out.println(username+password);
            client.getConnection(ar1->{
                if(ar1.succeeded()){
                    System.out.println("connected");
                    SqlConnection conn=ar1.result();
                    conn.preparedQuery("insert into user (username,password)values (?,?)")
                            .execute(Tuple.of(username,password),ar2->{
                                conn.close();
                                if(ar2.succeeded()){
                                    RowSet<Row> rows= ar2.result();
                                    System.out.println("success"+rows.rowCount());
                                }
                            });
                }
            });
            // 用户名密码为admin的才允许登录
            if(username!=null&&password!=null&&"admin".equals(username.trim())&&"admin".equals(password.trim())) {
                User user = User.create(new JsonObject().put("username", username).put("password", password));
                context.setUser(user);
                MyDb myDb=new MyDb();
                myDb.connect(vertx,username,password);
                //System.out.println((char[]) user.get("username"));
                context.json( new JsonObject().put("status", 200).put("msg", "login success!"));
            }else {
                context.json( new JsonObject().put("status", 500).put("msg", "login failed!"));
            }
        });
        router.route("/loginpage").handler(context->{
            context.json( new JsonObject().put("status", 200).put("msg", "please login!"));
        });
        RedirectAuthHandler authHandler=RedirectAuthHandler.create(authenticationProvider);
        router.route().handler(authHandler);
        router.route("/hello").method(HttpMethod.GET).handler(context->{
            context.json(new JsonObject("{\"status\":200,\"msg\":\"ok\"}"));
        });

        // Create the HTTP server
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(router)
                // Start listening
                .listen(8888)
                // Print the port
                .onSuccess(server ->
                        System.out.println(
                                "HTTP server started on port " + server.actualPort()
                        )
                );
    }
}
