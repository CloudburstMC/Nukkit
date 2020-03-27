package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemIds;
import cn.nukkit.math.BlockFace;
import com.nukkitx.math.vector.Vector3i;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (item.getMeta() > 0) {
            if (target.canBeFlooded()) {
                Block replace = Block.get(ItemBucket.getBlockIdFromDamage(item.getMeta()));

                if (replace instanceof BlockLiquid) {
                    block.getLevel().setBlock(target.getPosition(), replace);
                    return Item.get(ItemIds.BUCKET);
                }
            }
        } else if (target instanceof BlockLiquid && target.getMeta() == 0) {
            return Item.get(ItemIds.BUCKET, ItemBucket.getDamageFromIdentifier(target.getId()));
        }

        return super.dispense(position, block, face, item);
    }
}
