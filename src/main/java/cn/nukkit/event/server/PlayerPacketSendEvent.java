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

    public PlayerPacketSendEvent(Player player, BedrockPacket packet) {
        super(player);
        this.packet = packet;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BedrockPacket getPacket() {
        return packet;
    }
}