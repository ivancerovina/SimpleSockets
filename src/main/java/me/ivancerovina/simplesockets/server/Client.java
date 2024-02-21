package me.ivancerovina.simplesockets.server;

import me.ivancerovina.simplesockets.logger.Logger;
import me.ivancerovina.simplesockets.packet.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Client {
    private final Socket socket;
    private final SimpleSocketServer server;
    private final Logger logger;
    private final HashMap<String, Object> sessionData;
    private final ClientInputHandler inputHandler;
    private final ClientOutputHandler outputHandler;
    private final Thread clientInputThread, clientOutputThread;

    protected Client(Socket socket, SimpleSocketServer server) throws IOException {

        this.socket = socket;
        this.server = server;

        var address = socket.getRemoteSocketAddress();
        this.logger = new Logger("Client " + address.toString());

        this.sessionData = new HashMap<>();

        inputHandler = new ClientInputHandler(this);
        clientInputThread = new Thread(inputHandler);
        clientInputThread.start();

        outputHandler = new ClientOutputHandler(this);
        clientOutputThread = new Thread(outputHandler);
        clientOutputThread.start();

    }

    public void sendPacket(Packet packet) throws InterruptedException {
        outputHandler.queueData(packet);
    }

    public void sendKeepaliveRequest() throws InterruptedException {
        outputHandler.queueKeepalive();
    }

    public void closeConnection() throws IOException {
        sessionData.clear();
        clientInputThread.interrupt();
        clientOutputThread.interrupt();
        socket.close();
        server.clients.remove(this);
        logger.info("connection closed");
    }

    protected ClientOutputHandler getOutputHandler() {
        return outputHandler;
    }

    protected ClientInputHandler getInputHandler() {
        return inputHandler;
    }

    public HashMap<String, Object> getSessionData() {
        return sessionData;
    }

    public SimpleSocketServer getServer() {
        return server;
    }

    public Socket getSocket() {
        return socket;
    }

    public Logger getLogger() {
        return logger;
    }
}
