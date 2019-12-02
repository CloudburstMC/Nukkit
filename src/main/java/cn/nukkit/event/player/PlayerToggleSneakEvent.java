package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleSneakEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected final boolean isSneaking;

    public PlayerToggleSneakEvent(Player player, boolean isSneaking) {
        this.player = player;
        this.isSneaking = isSneaking;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isSneaking() {
        return this.isSneaking;
    }

}
