package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.math.Vector3;

/**
 * @author joserobjr
 */
public class ItemHoneyBottle extends ItemEdible {
    
    public ItemHoneyBottle() {
        this(0, 1);
    }
    
    public ItemHoneyBottle(Integer meta) {
        this(meta, 1);
    }
    
    public ItemHoneyBottle(Integer meta, int count) {
        super(HONEY_BOTTLE, meta, count, "Honey Bottle");
    }
    
    @Override
    public int getMaxStackSize() {
        return 16;
    }
    
    @PowerNukkitDifference(since = "1.3.2.0-PN", info = "Will always return true so it's always drinkable")
    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @PowerNukkitDifference(since = "1.3.2.0-PN", 
            info = "Cancellable by PlayerItemConsumeEvent and uses the FoodHoney class to handle the food behaviour")
    @Override
    public boolean onUse(Player player, int ticksUsed) {
        return super.onUse(player, ticksUsed);
    }
}
