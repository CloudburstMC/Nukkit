package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author CreeperFace
 */
public class ItemRedstoneComparator extends Item {

    public ItemRedstoneComparator() {
        this(0);
    }

    public ItemRedstoneComparator(Integer meta) {
        this(0, 1);
    }

    public ItemRedstoneComparator(Integer meta, int count) {
        super(COMPARATOR, meta, count, "Redstone Comparator");
        this.block = Block.get(BlockID.UNPOWERED_COMPARATOR);
    }
}