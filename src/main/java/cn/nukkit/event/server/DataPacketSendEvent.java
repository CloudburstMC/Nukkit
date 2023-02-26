package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DataPacketSendEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BedrockPacket packet;
    private final Player player;

    public DataPacketSendEvent(Player player, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public BedrockPacket getPacket() {
        return packet;
    }
}
