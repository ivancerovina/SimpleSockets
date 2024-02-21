package me.ivancerovina.simplesockets.packet;

public interface PacketProtocolHandler {
    void onMessageReceived(byte[] data);
    void onKeepaliveReceived();
}
