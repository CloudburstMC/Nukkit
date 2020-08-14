package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;

/**
 * @author Kaooot
 * @version 1.0
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
        super.onUse(player, ticksUsed);

        if (player.hasEffect(Effect.POISON)) {
            player.removeEffect(Effect.POISON);
        }

        player.getInventory().setItemInHand(this);

        if (!player.isCreative()) {
            player.getInventory().addItem(new Item(ItemID.BOTTLE, 0, 1));
        }
        return true;
    }
}