package com.demo.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;


public class MainVerticle extends AbstractVerticle {
    boolean login=false;
    public void start(){
        //Vertx vertx=Vertx.vertx();
        Router route= Router.router(vertx);
        route.route("/login").handler(context->{
            MultiMap queryParams=context.queryParams();
            String username= queryParams.get("username");
            String password=queryParams.get("password");
            User user=User.create(new JsonObject().put("username",username).put("password",password));
            context.setUser(user);
            login=true;
            context.json(new JsonObject().put("status",200).put("msg","login success!"));
        });
        route.route().handler(context->{
            User user=context.user();
            System.out.println(user);
            if(!login){
                System.out.println("认证失败");
                context.json(new JsonObject().put("status",500).put("msg","no login"));
            }else {
                context.next();
            }
        });
        route.route("/logout").handler(context->{
            login=false;
            context.json(new JsonObject().put("status",200).put("msg","login out success"));
        });
        //执行请求
        route.route("/hello").method(HttpMethod.GET).handler(context->{
            context.json(new JsonObject("{\"status\":200,\"msg\":\"ok\"}"));
        });
        vertx.createHttpServer()
                .requestHandler(route)
                .listen(9999)
                .onSuccess(server->{
                    System.out.println("HTTP server started on port"+server.actualPort());
                });
    }
}
