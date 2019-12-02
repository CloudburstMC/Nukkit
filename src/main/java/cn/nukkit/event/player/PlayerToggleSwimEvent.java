package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @author CreeperFace
 */
public class PlayerToggleSwimEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final boolean isSwimming;

    public PlayerToggleSwimEvent(Player player, boolean isSwimming) {
        this.player = player;
        this.isSwimming = isSwimming;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isSwimming() {
        return this.isSwimming;
    }
}
