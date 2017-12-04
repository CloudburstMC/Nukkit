package cn.nukkit.server.item.food;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.ItemBucket;

/**
 * Created by Snake1999 on 2016/1/21.
 * Package cn.nukkit.server.item.food in project nukkit.
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
