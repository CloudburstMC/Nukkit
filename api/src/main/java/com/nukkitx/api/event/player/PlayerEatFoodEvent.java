package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemStack;

public class PlayerEatFoodEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemStack food;
    private float foodLevelAdded;
    private int saturationAdded;
    private boolean cancelled = false;

    public PlayerEatFoodEvent(final Player player, ItemStack food, float foodLevelAdded, int saturationAdded) {
        this.player = player;
        this.food = food;
        this.foodLevelAdded = foodLevelAdded;
        this.saturationAdded = saturationAdded;
    }

    public ItemStack getFood() {
        return food;
    }

    public float getFoodLevelAdded() {
        return foodLevelAdded;
    }

    public void setFoodLevelAdded(float foodLevelAdded) {
        this.foodLevelAdded = foodLevelAdded;
    }

    public int getSaturationAdded() {
        return saturationAdded;
    }

    public void setSaturationAdded(int staturationAdded) {
        this.saturationAdded = staturationAdded;
    }

    @Override
    public Player getPlayer() {
        return player;
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
