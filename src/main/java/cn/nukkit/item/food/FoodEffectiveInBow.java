package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.ItemBowl;

public class FoodEffectiveInBow extends FoodEffective {
    public FoodEffectiveInBow(int restoreFood, float restoreSaturation) {
        super(restoreFood, restoreSaturation);
    }
    
    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(new ItemBowl());
        return true;
    }
}
