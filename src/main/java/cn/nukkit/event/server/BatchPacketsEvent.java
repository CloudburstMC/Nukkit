package cn.nukkit.event.server;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.player.Player;

public class BatchPacketsEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Player[] players;
    private DataPacket[] packets;

    private boolean forceSync;

    public BatchPacketsEvent(Player[] players, DataPacket[] packets, boolean forceSync) {
        this.players = players;
        this.packets = packets;
        this.forceSync = forceSync;
    }

    public Player[] getPlayers() {
        return players;
    }

    public DataPacket[] getPackets() {
        return packets;
    }

    public boolean isForceSync() {
        return forceSync;
    }
}
