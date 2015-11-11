package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.permission.Permissible;

import java.util.Set;

public class PlayerFoodLevelChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected int foodLevel;
    protected int FSL;

    public PlayerFoodLevelChangeEvent(Player player, int foodLevel, int FSL) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.FSL = FSL;
    }

    public int getFoodLevel() {
        return  this.foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public int getFSL() {
        return this.FSL;
    }

    public void setFSL(int FSL) {
        this.FSL = FSL;
    }

}
