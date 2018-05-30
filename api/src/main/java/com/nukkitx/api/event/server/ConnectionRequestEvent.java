package com.nukkitx.api.event.server;

import com.nukkitx.api.event.Event;

import java.net.InetSocketAddress;

/**
 * Called when RakNet receives a connection request packet.
 * This is the earliest you can deny someone from entering the server
 */
public class ConnectionRequestEvent implements Event {
    private final InetSocketAddress address;
    private Result result;

    public ConnectionRequestEvent(InetSocketAddress address, Result result) {
        this.address = address;
        this.result = result;
    }

    /**
     * Get address of connecting user.
     * @return address
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Get current result of the user connecting.
     * @return result
     */
    public Result getResult() {
        return result;
    }

    /**
     * Set current result of the user connecting.
     * @param result
     */
    public void setResult(Result result) {
        this.result = result;
    }

    public enum Result {
        /**
         * Connecting user will continue to login.
         */
        CONTINUE,
        /**
         * Connecting user will be sent banned packet.
         */
        BANNED,
        /**
         * Connecting user will be sent no free connections packet.
         */
        SERVER_FULL,
    }
}
