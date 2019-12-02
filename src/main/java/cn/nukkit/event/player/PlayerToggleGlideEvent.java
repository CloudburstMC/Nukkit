package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleGlideEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected final boolean isGliding;

    public PlayerToggleGlideEvent(Player player, boolean isSneaking) {
        this.player = player;
        this.isGliding = isSneaking;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isGliding() {
        return this.isGliding;
    }

}
