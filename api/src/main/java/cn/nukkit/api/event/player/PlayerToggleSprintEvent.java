package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class PlayerToggleSprintEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final boolean isSprinting;

    public PlayerToggleSprintEvent(Player player, boolean isSprinting) {
        this.player = player;
        this.isSprinting = isSprinting;
    }

    public boolean isSprinting() {
        return this.isSprinting;
    }

}
