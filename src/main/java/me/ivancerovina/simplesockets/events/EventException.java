package me.ivancerovina.simplesockets.events;

public class EventException extends RuntimeException {
    public EventException(String message) {
        super(message);
    }

    public EventException(Throwable throwable) {
        super(throwable);
    }

    public EventException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
