package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DataPacketReceiveEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final BedrockPacket packet;
    private final Player player;

    public DataPacketReceiveEvent(Player player, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public BedrockPacket getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
