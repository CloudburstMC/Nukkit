package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;

/**
 * Just before a player is sent to another server with the transfer packet.
 */
public class PlayerTransferEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final String address;
    private final int port;
    private boolean cancelled = false;

    public PlayerTransferEvent(Player player, String address, int port) {
        this.player = player;
        this.address = address;
        this.port = port;
    }

    /**
     * Player to be transferred.
     *
     * @return player
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Check if the transfer will be cancelled.
     *
     * @return transfer cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set whether the transfer will be cancelled.
     *
     * @param cancelled true to cancel the transfer.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Get the address which the player will be sent to.
     *
     * @return address of target server
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the port which the player will be sent to.
     *
     * @return port of target server
     */
    public int getPort() {
        return port;
    }
}
