package me.ivancerovina.simplesockets.packet;

import java.nio.ByteBuffer;

/**
 * Represents a protocol for handling packets. This protocol
 * is responsible for framing incoming data into packets
 * and notifying a handler when complete packets are received.
 * @author Stephen Cleary - Used his idea from his blog {@link <a href="https://blog.stephencleary.com/2009/04/message-framing.html">Stephen Cleary's blog post</a> }
 * @
 */
public class PacketProtocol {
    public static final byte[] KEEPALIVE_PACKET;
    private final int maxMessageSize;
    private final PacketProtocolHandler handler;

    private final byte[] lengthBuffer = new byte[Integer.BYTES];
    private byte[] dataBuffer = null;
    private int bytesReceived = 0;

    static {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(0);
        KEEPALIVE_PACKET = buffer.array();
    }

    /**
     * Constructs a new PacketProtocol instance.
     *
     * @param maxMessageSize The maximum size of a message, in bytes.
     * @param handler        The handler to notify when complete packets are received.
     */
    public PacketProtocol(int maxMessageSize, PacketProtocolHandler handler) {
        this.maxMessageSize = maxMessageSize;
        this.handler = handler;
    }

    /**
     * Appends incoming data to the protocol for processing.
     *
     * @param data The incoming data.
     */
    public synchronized void appendData(byte[] data) throws ProtocolViolationException {
        int i = 0;

        while (i != data.length) {
            int bytesAvailable = data.length - i;

            if (this.dataBuffer != null) {
                int bytesRequested = this.dataBuffer.length - this.bytesReceived;
                int bytesTransferred = Math.min(bytesRequested, bytesAvailable);
                System.arraycopy(data, i, this.dataBuffer, this.bytesReceived, bytesTransferred);
                i += bytesTransferred;
                this.bytesReceived += bytesTransferred;
                if (this.bytesReceived == this.dataBuffer.length) {
                    readComplete();
                }
            } else {
                int bytesRequested = this.lengthBuffer.length - this.bytesReceived;
                int bytesTransferred = Math.min(bytesRequested, bytesAvailable);
                System.arraycopy(data, i, this.lengthBuffer, this.bytesReceived, bytesTransferred);

                i += bytesTransferred;
                this.bytesReceived += bytesTransferred;
                if (this.bytesReceived == Integer.BYTES) {
                    readComplete();
                }
            }
        }
    }

    /**
     * Handles the completion of reading a message.
     */
    private void readComplete() {
        if (this.dataBuffer == null) {
            int length = ByteBuffer.wrap(lengthBuffer).getInt();

            if (length < 0) {
                throw new ProtocolViolationException("Message length is less than zero");
            }

            if (this.maxMessageSize > 0 && length > this.maxMessageSize) {
                throw new ProtocolViolationException("Message length " + length + " is larger than the maximum message size " + this.maxMessageSize);
            }

            if (length == 0) {
                this.bytesReceived = 0;

                if (handler != null) {
                    handler.onKeepaliveReceived();
                }
            } else {
                this.dataBuffer = new byte[length];
                this.bytesReceived = 0;
            }
        } else {
            if (handler != null) {
                handler.onMessageReceived(this.dataBuffer);
            }

            this.dataBuffer = null;
            this.bytesReceived = 0;
        }
    }

    /**
     * Wraps a message with a length prefix.
     *
     * @param data The message data.
     * @return The wrapped message.
     */
    public static byte[] wrapMessage(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(data.length);
        byte[] lengthPrefix = buffer.array();

        byte[] result = new byte[data.length + lengthPrefix.length];
        System.arraycopy(lengthPrefix, 0, result, 0, lengthPrefix.length);
        System.arraycopy(data, 0, result, lengthPrefix.length, data.length);

        return result;
    }
}