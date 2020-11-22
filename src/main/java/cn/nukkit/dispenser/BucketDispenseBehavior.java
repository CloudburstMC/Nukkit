package cn.nukkit.dispenser;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        ItemBucket bucket = (ItemBucket) item;

        if (!bucket.isEmpty()) {
            if (target.canBeFlowedInto()) {
                Block replace = bucket.getTargetBlock();

                if (replace instanceof BlockLiquid) {
                    block.level.setBlock(target, replace);
                    return Item.get(ItemID.BUCKET);
                }
            }
        } else if (target instanceof BlockWater && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            return Item.get(ItemID.WATER_BUCKET);
        } else if (target instanceof BlockLava && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            return Item.get(ItemID.LAVA_BUCKET);
        }

        return super.dispense(block, face, item);
    }
}
