package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

/**
 * @author CreeperFace
 */
public class ItemRedstoneComparator extends Item {

    public ItemRedstoneComparator(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.UNPOWERED_COMPARATOR);
    }
}