package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BeetrootSeeds extends Item {

    public BeetrootSeeds() {
        this(0, 1);
    }

    public BeetrootSeeds(int meta) {
        this(meta, 1);
    }

    public BeetrootSeeds(int meta, int count) {
        super(BEETROOT_SEEDS, 0, count, "Beetroot Seeds");
        this.block = Block.get(Item.BEETROOT_BLOCK);
    }
}
