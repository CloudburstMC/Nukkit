package cn.nukkit.dispenser;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
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
            return MinecraftItemID.WATER_BUCKET.get(1, item.getCompoundTag());
        } else if (target instanceof BlockLava && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            return MinecraftItemID.LAVA_BUCKET.get(1, item.getCompoundTag());
        }

        super.dispense(block, face, item);
        return null;
    }
}
