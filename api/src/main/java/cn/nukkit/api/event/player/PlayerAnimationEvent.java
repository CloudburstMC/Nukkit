package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;

public class PlayerAnimationEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Player.Animation animation;
    private boolean cancelled;

    public PlayerAnimationEvent(Player player, Player.Animation animation) {
        this.player = player;
        this.animation = animation;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Player.Animation getAnimation() {
        return animation;
    }
}
