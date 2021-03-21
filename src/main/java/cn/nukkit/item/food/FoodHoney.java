package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemGlassBottle;
import cn.nukkit.potion.Effect;

@PowerNukkitOnly
public class FoodHoney extends Food {
    public FoodHoney(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }
    
    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(new ItemGlassBottle());
        player.removeEffect(Effect.POISON);
        return true;
    }
}
