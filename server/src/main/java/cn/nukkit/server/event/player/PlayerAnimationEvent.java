package cn.nukkit.server.event.player;

import cn.nukkit.server.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public static final int ARM_SWING = 1;

    private final int animationType;

    public PlayerAnimationEvent(Player player) {
        this(player, ARM_SWING);
    }

    public PlayerAnimationEvent(Player player, int animation) {
        this.player = player;
        this.animationType = animation;
    }

    public int getAnimationType() {
        return this.animationType;
    }
}
