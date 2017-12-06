package cn.nukkit.api.event.server;

import cn.nukkit.api.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.network.protocol.DataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DataPacketReceiveEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final DataPacket packet;
    private final Player player;

    public DataPacketReceiveEvent(Player player, DataPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public DataPacket getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
    }
}
