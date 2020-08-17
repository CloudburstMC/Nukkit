package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.ItemBucket;

/**
 * @author Snake1999
 * @since 2016/1/21
 */
public class FoodMilk extends Food {
    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(new ItemBucket());
        player.removeAllEffects();
        return true;
    }
}
