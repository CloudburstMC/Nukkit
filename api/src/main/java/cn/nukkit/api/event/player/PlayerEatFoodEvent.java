package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class PlayerEatFoodEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemInstance food;
    private float foodLevelAdded;
    private int saturationAdded;
    private boolean cancelled = false;

    public PlayerEatFoodEvent(final Player player, ItemInstance food, float foodLevelAdded, int saturationAdded) {
        this.player = player;
        this.food = food;
        this.foodLevelAdded = foodLevelAdded;
        this.saturationAdded = saturationAdded;
    }

    public ItemInstance getFood() {
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
