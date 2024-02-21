package me.ivancerovina.simplesockets.client;

import me.ivancerovina.simplesockets.events.EventManager;
import me.ivancerovina.simplesockets.logger.Logger;
import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.packet.PacketManager;
import me.ivancerovina.simplesockets.packet.PacketProtocol;
import me.ivancerovina.simplesockets.packet.PacketProtocolHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SimpleSocketClient {
    private final Logger logger = new Logger("Client");
    private final PacketManager packetManager;
    private final EventManager eventManager;
    private final Socket socket;
    private InputHandler inputHandler;
    private OutputHandler outputHandler;

    private Thread inputHandlerThread;
    private Thread outputHandlerThread;

    public SimpleSocketClient() {

        this.packetManager = new PacketManager();
        this.eventManager = new EventManager();
        this.socket = new Socket();
    }

    public void connect(InetSocketAddress address) throws IOException {
        if (inputHandlerThread != null) {
            inputHandlerThread.interrupt();
            inputHandlerThread = null;
            inputHandler = null;
        }

        if (outputHandlerThread != null) {
            outputHandlerThread.interrupt();
            outputHandlerThread = null;
            inputHandler = null;
        }

        this.socket.connect(address);
        logger.info("Connected to " + address.getAddress().getHostName() + ":" + address.getPort());

        inputHandler = new InputHandler(this);
        inputHandlerThread = new Thread(inputHandler);
        inputHandlerThread.start();
        logger.info("Started: input handler thread");

        outputHandler = new OutputHandler(this);
        outputHandlerThread = new Thread(outputHandler);
        outputHandlerThread.start();
        logger.info("Started: output handler thread");
    }

    public void sendPacket(Packet packet) throws IOException, InterruptedException {
        outputHandler.queueData(packet);
    }

    public void closeConnection() throws IOException {
        socket.close();
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public Socket getSocket() {
        return socket;
    }

    public Logger getLogger() {
        return logger;
    }
}
