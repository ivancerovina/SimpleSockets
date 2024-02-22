package me.ivancerovina.simplesockets.server.events;

import me.ivancerovina.simplesockets.events.Event;
import me.ivancerovina.simplesockets.server.Client;

public class ClientExceptionEvent implements Event {
    private final Client client;
    private final Throwable throwable;

    public ClientExceptionEvent(Client client, Throwable throwable) {
        this.client = client;
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Client getClient() {
        return client;
    }
}
