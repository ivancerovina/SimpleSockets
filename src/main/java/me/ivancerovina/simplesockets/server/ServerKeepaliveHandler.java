package me.ivancerovina.simplesockets.server;

import java.io.IOException;

public class ServerKeepaliveHandler implements Runnable {
    private final SimpleSocketServer server;
    private final long keepaliveTimeout;

    public ServerKeepaliveHandler(SimpleSocketServer server, long keepaliveTimeout) {
        this.server = server;
        this.keepaliveTimeout = keepaliveTimeout;
    }

    @Override
    public void run() {
        for (Client client : server.getClients()) {
            var session = client.getSessionData();
            var socket = client.getSocket();

            if (!socket.isConnected() || socket.isClosed()) {
                closeClient(client);
                return;
            }

            if (!session.containsKey("keepalive_check_time")) {
                session.put("keepalive_check_time", System.currentTimeMillis());
                continue;
            }

            var timeDiff = System.currentTimeMillis() - (long) session.get("keepalive_check_time");

            if (timeDiff > keepaliveTimeout) {
                if ((boolean) session.getOrDefault("keepalive_requested", false)) {
                    closeClient(client);
                } else {
                    session.put("keepalive_requested", true);
                    try {
                        client.sendKeepaliveRequest();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        try {
            Thread.sleep(keepaliveTimeout / 2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void keepaliveReceived(Client client) {
        var session = client.getSessionData();
        session.put("keepalive_requested", false);
        session.put("keepalive_check_time", System.currentTimeMillis());
    }

    private void closeClient(Client client) {
        try {
            client.closeConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
