package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (item.getDamage() > 0) {
            if (target.canBeFlowedInto()) {
                Block replace = Block.get(ItemBucket.getBlockByDamage(item.getDamage()));

                if (replace instanceof BlockLiquid) {
                    if (block.level.getDimension() == Level.DIMENSION_NETHER) {
                        replace = Block.get(Block.AIR);
                        block.level.addParticle(new SmokeParticle(target.add(0.5, 0.5, 0.5)), null, 4);
                    }
                    block.level.setBlock(target, replace);
                    return Item.get(Item.BUCKET);
                }
            }
        } else if (target instanceof BlockLiquid && target.getDamage() == 0) {
            target.level.setBlock(target, Block.get(BlockID.AIR));
            return Item.get(Item.BUCKET, ItemBucket.getDamageByTarget(target.getId()));
        }

        return super.dispense(block, face, item);
    }
}
