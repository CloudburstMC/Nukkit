package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WheatSeeds extends Item {

    public WheatSeeds() {
        this(0, 1);
    }

    public WheatSeeds(int meta) {
        this(meta, 1);
    }

    public WheatSeeds(int meta, int count) {
        super(WHEAT_SEEDS, 0, count, "Wheat Seeds");
        this.block = Block.get(Item.WHEAT_BLOCK);
    }
}
