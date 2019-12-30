package cn.nukkit.item.food;

import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

import static cn.nukkit.item.ItemIds.BOWL;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item.food in project nukkit.
 */
public class FoodInBowl extends Food {

    public FoodInBowl(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(Item.get(BOWL));
        return true;
    }

}
