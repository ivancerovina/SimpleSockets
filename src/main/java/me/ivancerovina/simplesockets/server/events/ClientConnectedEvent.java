package me.ivancerovina.simplesockets.server.events;

import me.ivancerovina.simplesockets.events.Cancellable;
import me.ivancerovina.simplesockets.events.Event;
import me.ivancerovina.simplesockets.server.Client;

public class ClientConnectedEvent implements Event, Cancellable {
    private final Client client;
    private boolean cancelled = false;

    public ClientConnectedEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void cancelEvent() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
