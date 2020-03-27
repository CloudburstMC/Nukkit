package cn.nukkit.item.food;

import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;

import static cn.nukkit.item.ItemIds.GLASS_BOTTLE;

public class FoodHoney extends Food {
    public FoodHoney(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(Item.get(GLASS_BOTTLE));
        player.removeEffect(Effect.POISON);
        return true;
    }
}
