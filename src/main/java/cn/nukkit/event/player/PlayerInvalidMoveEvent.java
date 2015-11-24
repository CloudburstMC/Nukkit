package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * call when a player moves wrongly
 *
 * @author WilliamGao
 * @version 0.1 (23/11/2015)
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

    public boolean isRevert(){
        return this.revert;
    }

    public void setRevert(boolean revert) {
        this.revert = revert;
    }

}
