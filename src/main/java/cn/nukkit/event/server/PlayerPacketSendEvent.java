package cn.nukkit.event.server;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerPacketSendEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final BedrockPacket packet;
    private final Player player;

    public PlayerPacketSendEvent(Player player, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public BedrockPacket getPacket() {
        return packet;
    }
}