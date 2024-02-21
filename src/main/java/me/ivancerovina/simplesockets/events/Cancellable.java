package me.ivancerovina.simplesockets.events;

public interface Cancellable {

    void cancelEvent();
    boolean isCancelled();
}
