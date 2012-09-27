package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastAckCallback;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.VoidAckCallback;
import com.corundumstudio.socketio.listener.DataListener;

public class AckChatLauncher {

    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
        server.addJsonObjectListener(ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(final SocketIOClient client, ChatObject data, final AckRequest ackRequest) {

                // check is ack requested by client
                if (ackRequest.isAckRequested()) {
                    // send ack response with data to client
                    ackRequest.sendAckData("client message was delivered to server!", "yeah!");
                }

                // send message back to client with ack callback WITH data
                ChatObject ackChatObjectData = new ChatObject(data.getUserName(), "message with ack data");
                client.sendJsonObject(ackChatObjectData, new AckCallback<String>(String.class) {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println("ack from client: " + client.getSessionId() + " data: " + result);
                    }
                });

                // send message back to client with ack callback WITHOUT data
                ChatObject ackChatObject = new ChatObject(data.getUserName(), "message without ack data");
                client.sendJsonObject(ackChatObject, new VoidAckCallback() {
                    @Override
                    public void onSuccess() {
                        System.out.println("ack from client: " + client.getSessionId());
                    }
                });

                // send broadcast message to clients with broadcast-ack
                ChatObject broadcastObject = new ChatObject(data.getUserName(), "broadcast message with ack data");
                server.getBroadcastOperations().sendJsonObject(broadcastObject, new BroadcastAckCallback<String>(String.class) {
                    @Override
                    protected void onClientSuccess(SocketIOClient client, String result) {
                        System.out.println("ack from client in broadcast: " + client.getSessionId() + " data: " + result);
                    }

                    @Override
                    protected void onClientTimeout(SocketIOClient client) {
                        System.out.println("ack timeout from client in broadcast: " + client.getSessionId());
                    }

                    @Override
                    protected void onAllSuccess() {
                        System.out.println("all broadcast ack delivered to clients!");
                    }
                });

            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}
