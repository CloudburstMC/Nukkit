package com.nukkitx.server.event.server;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.event.server.ServerEvent;
import com.nukkitx.protocol.bedrock.BedrockPacket;

public class NetworkPacketReceiveEvent implements ServerEvent, Cancellable {
    private final BedrockPacket packet;
    private final Player player;
    private boolean cancelled;

    public NetworkPacketReceiveEvent(Player player, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public BedrockPacket getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
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
