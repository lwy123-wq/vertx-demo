package com.demo;

import com.demo.vertx.MainVerticle;
import com.demo.vertx.SessionVerticle;
import io.vertx.core.Vertx;

public class MyMain {
    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        vertx.deployVerticle(new SessionVerticle());
    }
}
