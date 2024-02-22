package me.ivancerovina.simplesockets.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.ivancerovina.simplesockets.client.events.ExceptionEvent;
import me.ivancerovina.simplesockets.client.events.PacketReceivedEvent;
import me.ivancerovina.simplesockets.demo.DemoPacket;
import me.ivancerovina.simplesockets.packet.PacketProtocol;
import me.ivancerovina.simplesockets.packet.PacketProtocolHandler;
import me.ivancerovina.simplesockets.server.events.ClientKeepaliveReceived;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Arrays;

public class InputHandler implements Runnable, PacketProtocolHandler {
    private final SimpleSocketClient client;
    private final InputStream inputStream;
    private final PacketProtocol packetProtocol;

    public InputHandler(SimpleSocketClient client) throws IOException {
        this.client = client;
        this.inputStream = client.getSocket().getInputStream();
        this.packetProtocol = new PacketProtocol(1024, this);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];

        while (true) {
            try {
                if (client.getSocket().isClosed() || !client.getSocket().isConnected()) {
                    break;
                }

                if (inputStream.available() > 0) {
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead != -1) {
                        byte[] data = Arrays.copyOf(buffer, bytesRead);
                        packetProtocol.appendData(data); // TODO handle ProtocolViolationException
                        // Data is no longer needed, so we can discard it
                        Arrays.fill(buffer, (byte) 0);
                    }
                } else {
                    Thread.sleep(10);
                }
            } catch (SocketException e) {
                break;
            } catch (IOException | InterruptedException e) {
                var event = new ExceptionEvent(e);

                if (!client.getEventManager().callEvent(event)) {
                    client.getLogger().error("An error occurred while writing data", e);
                }
            }
        }

        try {
            client.closeConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived(byte[] data) {
        try {
            var packet = client.getPacketManager().deserializePacket(data);

            var event = new PacketReceivedEvent(packet);
            client.getEventManager().callEvent(event);

        } catch (JsonProcessingException ignored) {
        }
    }

    @Override
    public void onKeepaliveReceived() {
        client.getLogger().info("Keepalive request received, sending...");
        client.getOutputHandler().sendKeepalive();
    }
}
