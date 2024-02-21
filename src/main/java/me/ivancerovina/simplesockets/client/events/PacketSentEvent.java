package me.ivancerovina.simplesockets.client.events;

import me.ivancerovina.simplesockets.events.Event;
import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.server.Client;

public class PacketSentEvent implements Event {
    private final Packet packet;

    public PacketSentEvent(Client client, Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
