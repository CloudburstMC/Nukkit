package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.protocol.AnimatePacket;

public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final AnimatePacket.Action animationType;

    public PlayerAnimationEvent(Player player) {
        this(player, AnimatePacket.Action.SWING_ARM);
    }

    public PlayerAnimationEvent(Player player, AnimatePacket.Action animation) {
        this.player = player;
        this.animationType = animation;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public AnimatePacket.Action getAnimationType() {
        return this.animationType;
    }
}
