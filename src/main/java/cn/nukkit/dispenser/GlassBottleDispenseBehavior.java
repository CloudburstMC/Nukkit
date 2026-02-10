package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockWater;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;

public class GlassBottleDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target instanceof BlockBeehive) {
            if (target.onActivate(item, null)) {
                return Item.get(Item.HONEY_BOTTLE);
            }
            return item;
        }

        if (target instanceof BlockWater && target.getDamage() == 0) {
            return Item.get(ItemID.POTION, 0, 1);
        }

        return super.dispense(block, face, item);
    }
}
