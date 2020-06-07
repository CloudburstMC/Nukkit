package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author CreeperFace
 */
public class ItemRedstoneRepeater extends Item {

    public ItemRedstoneRepeater() {
        this(0);
    }

    public ItemRedstoneRepeater(Integer meta) {
        this(0, 1);
    }

    public ItemRedstoneRepeater(Integer meta, int count) {
        super(REPEATER, meta, count, "Redstone Repeater");
        this.block = Block.get(BlockID.UNPOWERED_REPEATER);
    }
}
