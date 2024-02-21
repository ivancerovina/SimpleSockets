package me.ivancerovina.simplesockets.server.events;

import me.ivancerovina.simplesockets.events.Event;
import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.server.Client;

public class PacketReceivedEvent implements Event {
    private final Client client;
    private final Packet packet;

    public PacketReceivedEvent(Client client, Packet packet) {
        this.client = client;
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public Client getClient() {
        return client;
    }
}
