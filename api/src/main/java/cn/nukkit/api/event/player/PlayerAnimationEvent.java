package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.event.Cancellable;

public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {

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
