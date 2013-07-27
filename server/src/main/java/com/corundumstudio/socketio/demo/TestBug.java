package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

public class TestBug {

    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setPort(9092);
        config.setTransports(Transport.XHRPOLLING);

        final SocketIOServer server = new SocketIOServer(config);
        server.addJsonObjectListener(ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                // broadcast messages to all clients
                server.getBroadcastOperations().sendJsonObject(data);
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}
