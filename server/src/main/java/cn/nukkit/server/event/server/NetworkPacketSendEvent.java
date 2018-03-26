package cn.nukkit.server.event.server;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.event.server.ServerEvent;
import cn.nukkit.server.network.raknet.NetworkPacket;

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
