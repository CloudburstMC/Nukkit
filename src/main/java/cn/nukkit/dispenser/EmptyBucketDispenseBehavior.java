package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemIds;
import cn.nukkit.math.BlockFace;
import com.nukkitx.math.vector.Vector3i;

/**
 * @author CreeperFace
 */
public class EmptyBucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target instanceof BlockLiquid && target.getMeta() == 0) {
            target.getLevel().setBlock(target.getPosition(), Block.get(BlockIds.AIR));
            return Item.get(ItemIds.BUCKET, ItemBucket.getDamageFromIdentifier(target.getId()));
        }

        super.dispense(position, block, face, item);
        return null;
    }
}
