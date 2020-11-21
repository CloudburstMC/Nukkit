package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

/**
 * Created by Snake1999 on 2016/1/21.
 * Package cn.nukkit.item.food in project nukkit.
 */
public class FoodMilk extends Food {
    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(Item.get(ItemID.BUCKET));
        player.removeAllEffects();
        return true;
    }
}
