package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * call when a player moves wrongly
 *
 * @author WilliamGao
 */

public class PlayerInvalidMoveEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean revert;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerInvalidMoveEvent(Player player, boolean revert) {
        this.player = player;
        this.revert = revert;
    }

    public boolean isRevert() {
        return this.revert;
    }

    /**
     * @deprecated If you just simply want to disable the movement check, please use {@link Player#setCheckMovement(boolean)} instead.
     * @param revert revert movement
     */
    @Deprecated
    public void setRevert(boolean revert) {
        this.revert = revert;
    }

}
