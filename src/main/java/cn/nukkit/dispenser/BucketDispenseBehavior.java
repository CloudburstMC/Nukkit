package cn.nukkit.dispenser;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        if (!(item instanceof ItemBucket)) {
            return super.dispense(block, face, item);
        }
        
        ItemBucket bucket = (ItemBucket) item; 
        Block target = block.getSide(face);
        
        if (!bucket.isEmpty()) {
            if (target.canBeFlowedInto() || target.getId() == BlockID.NETHER_PORTAL) {
                Block replace = bucket.getTargetBlock();

                if (replace instanceof BlockLiquid) {
                    if (target.getId() == BlockID.NETHER_PORTAL) {
                        target.onBreak(null);
                    }

                    block.level.setBlock(target, replace);
                    return MinecraftItemID.BUCKET.get(1, bucket.getCompoundTag());
                }
            }
        } else if (target instanceof BlockWater && target.getDamage() == 0) {
            target.level.setBlock(target, Block.get(BlockID.AIR));
            return MinecraftItemID.WATER_BUCKET.get(1, bucket.getCompoundTag());
        } else if (target instanceof BlockLava && target.getDamage() == 0) {
            target.level.setBlock(target, Block.get(BlockID.AIR));
            return MinecraftItemID.LAVA_BUCKET.get(1, bucket.getCompoundTag());
        }

        return super.dispense(block, face, item);
    }
}
