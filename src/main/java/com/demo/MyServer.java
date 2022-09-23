package com.demo;

import java.nio.Buffer;

/*
public class MyServer extends Verticle {
    @Override
    public void start() {
        //container.deployModule("");
        System.out.println("接收端启动了");
        EventBus eb=vertx.eventBus();
        Handler<Message> myHandler=new Handler<Message>() {
            public void handle(Message message) {
                JsonObject obj= (JsonObject) message.body();
                String data=obj.getString("data");
                System.out.println("接收端接收数据："+data);
                message.reply("接收端返回数据："+data);
            }
        };
        eb.registerHandler("com.city.oa.data",myHandler);
        System.out.println("数据接收端Verticle部署成功");

        vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
            public void handle(NetSocket netSocket) {
                Pump.createPump(netSocket,netSocket).start();
            }
        }).listen(10224);
    }
    public void stop(){
        System.out.println("TCP SERVER 要停止了");
    }
}
*/
