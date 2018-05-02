package com.nukkitx.server.event.server;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.event.server.ServerEvent;
import com.nukkitx.server.network.raknet.NetworkPacket;

public class NetworkPacketSendEvent implements ServerEvent, Cancellable {
    private final NetworkPacket packet;
    private final Player player;
    private boolean cancelled;

    public NetworkPacketSendEvent(Player player, NetworkPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public NetworkPacket getPacket() {
        return packet;
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
