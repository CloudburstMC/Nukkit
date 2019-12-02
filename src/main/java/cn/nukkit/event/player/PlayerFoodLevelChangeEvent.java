package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerFoodLevelChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected int foodLevel;
    protected float foodSaturationLevel;
    public PlayerFoodLevelChangeEvent(Player player, int foodLevel, float foodSaturationLevel) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.foodSaturationLevel = foodSaturationLevel;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public float getFoodSaturationLevel() {
        return foodSaturationLevel;
    }

    public void setFoodSaturationLevel(float foodSaturationLevel) {
        this.foodSaturationLevel = foodSaturationLevel;
    }
}
