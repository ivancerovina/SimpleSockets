package me.ivancerovina.simplesockets.client.events;

import me.ivancerovina.simplesockets.events.Event;
import me.ivancerovina.simplesockets.server.Client;

public class ExceptionEvent implements Event {
    private final Throwable throwable;

    public ExceptionEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
