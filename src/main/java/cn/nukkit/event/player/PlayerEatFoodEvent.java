package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.food.Food;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.event.player in project nukkit.
 */
public class PlayerEatFoodEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Food food;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerEatFoodEvent(Player player, Food food) {
        this.player = player;
        this.food = food;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

}
