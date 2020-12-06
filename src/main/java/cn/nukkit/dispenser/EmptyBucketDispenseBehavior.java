package cn.nukkit.dispenser;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class EmptyBucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target instanceof BlockWater && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            return Item.get(ItemID.WATER_BUCKET);
        } else if (target instanceof BlockLava && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            return Item.get(ItemID.LAVA_BUCKET);
        }

        super.dispense(block, face, item);
        return null;
    }
}
