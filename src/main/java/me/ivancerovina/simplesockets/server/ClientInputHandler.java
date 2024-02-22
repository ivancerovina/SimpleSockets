package me.ivancerovina.simplesockets.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.packet.PacketProtocol;
import me.ivancerovina.simplesockets.packet.PacketProtocolHandler;
import me.ivancerovina.simplesockets.server.events.ClientExceptionEvent;
import me.ivancerovina.simplesockets.server.events.ClientKeepaliveReceived;
import me.ivancerovina.simplesockets.server.events.PacketReceivedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientInputHandler implements Runnable, PacketProtocolHandler {
    private final Client client;
    private final InputStream inputStream;
    private final PacketProtocol packetProtocol;

    public ClientInputHandler(Client client) throws IOException {
        this.client = client;
        this.inputStream = client.getSocket().getInputStream();
        this.packetProtocol = new PacketProtocol(1024, this);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];

        while (true) {
            try {
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
                var event = new ClientExceptionEvent(client, e);

                if (!client.getServer().getEventManager().callEvent(event)) {
                    client.getLogger().error("An error occurred while reading data", e);
                }
                break;
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

    @Override
    public void onMessageReceived(byte[] data) {
        try {
            var packet = client.getServer().getPacketManager().deserializePacket(data);
            var event = new PacketReceivedEvent(client, packet);
            this.client.getServer().getEventManager().callEvent(event);

        } catch (JsonProcessingException ignored) {

        }
    }

    @Override
    public void onKeepaliveReceived() {
        if (client.getServer().getKeepaliveHandler() != null) {
            client.getServer().getKeepaliveHandler().keepaliveReceived(client);
        }

        client.getServer().getEventManager().callEvent(new ClientKeepaliveReceived(client));
    }
}
