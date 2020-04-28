package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;

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
        super(player);
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
