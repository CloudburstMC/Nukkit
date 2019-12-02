package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleFlightEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected final boolean isFlying;

    public PlayerToggleFlightEvent(Player player, boolean isFlying) {
        this.player = player;
        this.isFlying = isFlying;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isFlying() {
        return this.isFlying;
    }
}
