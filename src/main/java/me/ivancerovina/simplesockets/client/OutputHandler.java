package me.ivancerovina.simplesockets.client;

import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.packet.PacketProtocol;

import java.io.BufferedOutputStream;
import java.io.IOException;
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
                var data = queue.take();

                outputStream.write(data);
                outputStream.flush();

            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            client.closeConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
