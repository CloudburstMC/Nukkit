package cn.nukkit.item;

import cn.nukkit.block.Wheat;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WheatSeeds extends Item {

    public WheatSeeds() {
        this(0, 1);
    }

    public WheatSeeds(Integer meta) {
        this(meta, 1);
    }

    public WheatSeeds(Integer meta, int count) {
        super(WHEAT_SEEDS, 0, count, "Wheat Seeds");
        this.block = new Wheat();
    }
}
