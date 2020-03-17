package cn.nukkit.event.server;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;

public class BatchPacketsEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Player[] players;
    private BedrockPacket[] packets;

    private boolean forceSync;

    public BatchPacketsEvent(Player[] players, BedrockPacket[] packets, boolean forceSync) {
        this.players = players;
        this.packets = packets;
        this.forceSync = forceSync;
    }

    public Player[] getPlayers() {
        return players;
    }

    public BedrockPacket[] getPackets() {
        return packets;
    }

    public boolean isForceSync() {
        return forceSync;
    }
}
