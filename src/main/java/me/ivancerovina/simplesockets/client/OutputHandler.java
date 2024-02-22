package me.ivancerovina.simplesockets.client;

import me.ivancerovina.simplesockets.client.events.ExceptionEvent;
import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.packet.PacketProtocol;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputHandler implements Runnable {
    private final BufferedOutputStream outputStream;
    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>(10);
    private SimpleSocketClient client;

    public OutputHandler(SimpleSocketClient client) throws IOException {
        this.client = client;
        this.outputStream = new BufferedOutputStream(client.getSocket().getOutputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (client.getSocket().isClosed() || !client.getSocket().isConnected()) {
                    break;
                }

                var data = queue.take();

                outputStream.write(data);
                outputStream.flush();
            } catch (SocketException e) {
                break;
            } catch (InterruptedException | IOException e) {
                var event = new ExceptionEvent(e);

                if (!client.getEventManager().callEvent(event)) {
                    client.getLogger().error("An error occurred while writing data", e);
                }

                break;
            }
        }

        try {
            client.closeConnection();
        } catch (IOException e) {
            var event = new ExceptionEvent(e);

            if (!client.getEventManager().callEvent(event)) {
                client.getLogger().error("Exception while closing connection", e);
            }
        }
    }

    public void queueData(Packet packet) throws IOException, InterruptedException {
        var serialized = client.getPacketManager().serializePacket(packet);
        queue.put(serialized);
        client.getLogger().info("Sent packet " + packet.getClass().getSimpleName());
    }

    protected void sendKeepalive() {
        try {
            outputStream.write(PacketProtocol.KEEPALIVE_PACKET);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
