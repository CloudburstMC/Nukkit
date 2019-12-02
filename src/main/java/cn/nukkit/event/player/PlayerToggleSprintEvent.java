package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleSprintEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected final boolean isSprinting;

    public PlayerToggleSprintEvent(Player player, boolean isSprinting) {
        this.player = player;
        this.isSprinting = isSprinting;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isSprinting() {
        return this.isSprinting;
    }

}
