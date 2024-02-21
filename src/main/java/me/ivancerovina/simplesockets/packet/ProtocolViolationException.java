package me.ivancerovina.simplesockets.packet;

public class ProtocolViolationException extends RuntimeException {
    public ProtocolViolationException(Throwable throwable) {
        super(throwable);
    }

    public ProtocolViolationException(String message) {
        super(message);
    }

    public ProtocolViolationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
