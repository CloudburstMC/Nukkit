package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;

/**
 * @author Kaooot
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

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (super.onUse(player, ticksUsed)) {
            player.removeEffect(Effect.POISON);

            if (!player.isCreative()) {
                this.count--;
                player.getInventory().addItem(Item.get(Item.BOTTLE, 0, 1));
            }
        }
        return true;
    }
}
