package me.ivancerovina.simplesockets.server;

import me.ivancerovina.simplesockets.client.SimpleSocketClient;
import me.ivancerovina.simplesockets.server.events.ClientConnectedEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientConnectionHandler implements Runnable {
    private final SimpleSocketServer server;
    private final ServerSocket socket;

    protected ClientConnectionHandler(SimpleSocketServer server) {
        this.server = server;
        this.socket = server.getSocket();
    }

    @Override
    public void run() {
        while (true) {
            try {
                listenForConnection();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listenForConnection() throws IOException {
        var clientSocket = socket.accept();
        var client = new Client(clientSocket, server);

        var event = new ClientConnectedEvent(client);

        server.getEventManager().callEvent(event);

        if (event.isCancelled()) {
            client.getLogger().info("connection denied by event");
            clientSocket.close();
            return;
        }

        client.getLogger().info("connected");
        server.clients.add(client);
    }
}
