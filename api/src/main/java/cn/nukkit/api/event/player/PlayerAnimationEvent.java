package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {
    private static final int ARM_SWING = 1;

    private final int animationType;

    private boolean cancelled = false;

    public PlayerAnimationEvent(Player player) {
        this(player, ARM_SWING);
    }

    public PlayerAnimationEvent(Player player, int animation) {
        super(player);
        this.animationType = animation;
    }
}
