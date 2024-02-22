package me.ivancerovina.simplesockets.server.events;

import me.ivancerovina.simplesockets.server.Client;
import me.ivancerovina.simplesockets.server.SimpleSocketServer;

public class ServerExceptionEvent {
    private final SimpleSocketServer server;
    private final Throwable throwable;

    public ServerExceptionEvent(SimpleSocketServer server, Throwable throwable) {
        this.server = server;
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public SimpleSocketServer getServer() {
        return server;
    }
}
