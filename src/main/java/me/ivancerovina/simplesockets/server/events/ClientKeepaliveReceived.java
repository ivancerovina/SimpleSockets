package me.ivancerovina.simplesockets.server.events;

import me.ivancerovina.simplesockets.events.Event;
import me.ivancerovina.simplesockets.server.Client;

public class ClientKeepaliveReceived implements Event {
    private final Client client;

    public ClientKeepaliveReceived(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
