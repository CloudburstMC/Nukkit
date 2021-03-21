package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author CreeperFace
 * @since 13.5.2017
 */
public class ItemHopper extends Item {

    public ItemHopper() {
        this(0);
    }

    public ItemHopper(Integer meta) {
        this(meta, 1);
    }

    public ItemHopper(Integer meta, int count) {
        super(HOPPER, 0, count, "Hopper");
        this.block = Block.get(BlockID.HOPPER_BLOCK);
    }
}
