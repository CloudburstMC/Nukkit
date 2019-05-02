package com.nukkitx.api.event.server;

import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.event.Event;

import java.net.InetSocketAddress;

/**
 * Called when RakNet receives a connection request packet.
 * This is the earliest you can deny someone from entering the server
 */
public class ConnectionRequestEvent implements Event, Cancellable {
    private final InetSocketAddress address;
    private volatile boolean cancelled;

    public ConnectionRequestEvent(InetSocketAddress address) {
        this.address = address;
    }

    /**
     * Get address of connecting user.
     * @return address
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
