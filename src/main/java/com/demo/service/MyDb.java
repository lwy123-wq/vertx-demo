package com.demo.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;

public class MyDb extends AbstractVerticle {
    public void connect(Vertx vertx,String username,String password){
        MySQLConnectOptions connection=new MySQLConnectOptions()
                .setPort(3306)
                .setHost("localhost")
                .setDatabase("vertx")
                .setUser("root")
                .setPassword("lwy0328");
        PoolOptions poolOptions=new PoolOptions()
                .setMaxSize(5);
        //Router router=Router.router(vertx);
        MySQLPool pool= MySQLPool.pool(vertx,connection,poolOptions);
        pool.preparedQuery("insert into user (username,password)values (?,?)")
                .execute(Tuple.of(username,password),ar->{
                    if(ar.succeeded()){
                        RowSet<Row> rows= ar.result();
                        System.out.println(rows.rowCount());
                    }else {
                        System.out.println("Failure:"+ar.cause().getMessage());
                    }
                    pool.close();
                });
    }
}

