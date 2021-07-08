package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.protocol.AnimatePacket;

public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int animationType;

    public PlayerAnimationEvent(Player player) {
        this(player, AnimatePacket.ACTION_SWING_ARM);
    }

    public PlayerAnimationEvent(Player player, int animation) {
        this.player = player;
        this.animationType = animation;
    }

    public int getAnimationType() {
        return this.animationType;
    }
}
