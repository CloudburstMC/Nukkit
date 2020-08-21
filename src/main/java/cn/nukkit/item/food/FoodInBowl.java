package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.ItemBowl;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class FoodInBowl extends Food {

    public FoodInBowl(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(new ItemBowl());
        return true;
    }

}
