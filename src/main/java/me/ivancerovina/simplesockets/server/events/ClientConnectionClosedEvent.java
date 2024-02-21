package me.ivancerovina.simplesockets.server.events;

import me.ivancerovina.simplesockets.server.Client;

public class ClientConnectionClosedEvent {
    private final Client client;

    public ClientConnectionClosedEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
