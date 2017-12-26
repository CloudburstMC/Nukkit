package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.server.item.food.Food;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.server.event.player in project nukkit.
 */
public class PlayerEatFoodEvent extends PlayerEvent implements Cancellable {

    private Food food;
    private boolean cancelled = false;

    public PlayerEatFoodEvent(final Player player, Food food) {
        this.player = player;
        this.food = food;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
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
