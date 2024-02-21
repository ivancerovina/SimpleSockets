package me.ivancerovina.simplesockets.server;

import me.ivancerovina.simplesockets.events.EventManager;
import me.ivancerovina.simplesockets.logger.Logger;
import me.ivancerovina.simplesockets.packet.PacketManager;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleSocketServer {
    private Logger logger = new Logger("SocketServer");
    private final PacketManager packetManager;
    private final EventManager eventManager;
    private final ServerSocket socket;
    private final ClientConnectionHandler connectionHandler;
    private ServerKeepaliveHandler keepaliveHandler;
    private final Thread connectionHandlerThread;
    private Thread keepaliveHandlerThread;
    protected List<Client> clients = new ArrayList<>();

    public SimpleSocketServer() throws IOException {
        this(new ServerSocket());
    }

    public SimpleSocketServer(SSLServerSocketFactory sslServerSocketFactory) throws IOException {
        this(sslServerSocketFactory.createServerSocket());
    }

    protected SimpleSocketServer(ServerSocket socket) {
        this.packetManager = new PacketManager();
        this.eventManager = new EventManager();
        this.socket = socket;

        this.connectionHandler = new ClientConnectionHandler(this);
        this.connectionHandlerThread = new Thread(this.connectionHandler);

        this.keepaliveHandler = new ServerKeepaliveHandler(this, 60000);
        this.keepaliveHandlerThread = new Thread(this.connectionHandler);
    }

    public void bind(InetSocketAddress socketAddress) throws IOException {
        Objects.requireNonNull(socketAddress, "socketAddress");

        socket.bind(socketAddress);
        logger.info("Bound to " + socketAddress.getAddress().getHostName() + ":" + socketAddress.getPort());
    }

    public void useKeepaliveHandler(long timeoutMilliseconds) {
        keepaliveHandler = new ServerKeepaliveHandler(this, timeoutMilliseconds);
    }


    public void listen() {
        connectionHandlerThread.start();

        if (keepaliveHandler != null) {
            keepaliveHandlerThread = new Thread(keepaliveHandler);
            keepaliveHandlerThread.start();
        }

        logger.info("Listening for clients");
    }

    public void closeConnection() throws IOException {
        connectionHandlerThread.interrupt();
        keepaliveHandlerThread.interrupt();

        for (Client client : clients) {
            client.closeConnection();
        }

        socket.close();
    }

    protected ServerKeepaliveHandler getKeepaliveHandler() {
        return keepaliveHandler;
    }

    public List<Client> getClients() {
        return Collections.unmodifiableList(clients);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public ServerSocket getSocket() {
        return socket;
    }

    public Logger getLogger() {
        return logger;
    }
}
