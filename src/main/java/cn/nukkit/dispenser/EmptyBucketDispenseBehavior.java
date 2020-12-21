package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class EmptyBucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target instanceof BlockLiquid && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            return new ItemBucket(ItemBucket.getDamageByTarget(target.getId()));
        }

        super.dispense(block, face, item);
        return null;
    }
}
