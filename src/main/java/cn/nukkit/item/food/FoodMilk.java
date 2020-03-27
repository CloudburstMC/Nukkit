package cn.nukkit.item.food;

import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

import static cn.nukkit.item.ItemIds.BUCKET;

/**
 * Created by Snake1999 on 2016/1/21.
 * Package cn.nukkit.item.food in project nukkit.
 */
public class FoodMilk extends Food {
    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(Item.get(BUCKET));
        player.removeAllEffects();
        return true;
    }
}
