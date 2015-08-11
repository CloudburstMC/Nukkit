package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bed extends Item {

    public Bed() {
        this(0, 1);
    }

    public Bed(int meta) {
        this(meta, 1);
    }

    public Bed(int meta, int count) {
        super(BED, 0, count, "Bed");
        this.block = Block.get(Item.BED_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
