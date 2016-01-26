package cn.nukkit.item.food;

import cn.nukkit.Player;

/**
 * Created by Snake1999 on 2016/1/13.
 * Package cn.nukkit.item.food in project nukkit.
 */
public class FoodNormal extends Food {

    public FoodNormal(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        return super.onEatenBy(player);
    }
}
