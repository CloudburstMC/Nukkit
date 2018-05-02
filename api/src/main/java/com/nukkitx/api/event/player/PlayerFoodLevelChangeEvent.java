package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;

public class PlayerFoodLevelChangeEvent implements PlayerEvent, Cancellable {
    private Player player;
    protected int foodLevel;
    protected float foodSaturationLevel;
    private boolean cancelled;

    public PlayerFoodLevelChangeEvent(Player player, int foodLevel, float foodSaturationLevel) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.foodSaturationLevel = foodSaturationLevel;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public float getFoodSaturationLevel() {
        return foodSaturationLevel;
    }

    public void setFoodSaturationLevel(float foodSaturationLevel) {
        this.foodSaturationLevel = foodSaturationLevel;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
