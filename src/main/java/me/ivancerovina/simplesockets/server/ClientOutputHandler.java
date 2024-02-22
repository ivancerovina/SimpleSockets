package me.ivancerovina.simplesockets.server;

import me.ivancerovina.simplesockets.client.events.ExceptionEvent;
import me.ivancerovina.simplesockets.client.events.PacketSentEvent;
import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.packet.PacketManager;
import me.ivancerovina.simplesockets.packet.PacketProtocol;
import me.ivancerovina.simplesockets.server.events.ClientExceptionEvent;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientOutputHandler implements Runnable {
    private final Client client;
    private final BufferedOutputStream outputStream;
    private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>(10);

    public ClientOutputHandler(Client client) throws IOException {
        this.client = client;
        this.outputStream = new BufferedOutputStream(client.getSocket().getOutputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                var packet = queue.take();
                var data = packet instanceof Packet ?
                        this.client.getServer().getPacketManager().serializePacket((Packet) packet)
                        : (byte[]) packet;

                outputStream.write(data);
                outputStream.flush();

                if (packet instanceof Packet) {
                    var event = new PacketSentEvent(client, (Packet) packet);
                    this.client.getServer().getEventManager().callEvent(event);
                }
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                var event = new ClientExceptionEvent(client, e);

                if (!client.getServer().getEventManager().callEvent(event)) {
                    client.getLogger().error("An error occurred while writing data", e);
                }
            }
        }

        try {
            client.closeConnection();
        } catch (IOException e) {
            var event = new ClientExceptionEvent(client, e);

            if (!client.getServer().getEventManager().callEvent(event)) {
                client.getLogger().error("An error occurred while closing connection with client", e);
            }
        }
    }

    public void queueData(Packet packet) throws InterruptedException {
        queue.put(packet);
    }

    public void queueKeepalive() throws InterruptedException {
        queue.put(PacketProtocol.KEEPALIVE_PACKET);
    }
}
