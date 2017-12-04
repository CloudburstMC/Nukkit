package cn.nukkit.server.item.food;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.ItemBowl;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.server.item.food in project nukkit.
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
