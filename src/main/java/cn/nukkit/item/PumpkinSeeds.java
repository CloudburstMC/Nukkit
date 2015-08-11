package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PumpkinSeeds extends Item {

    public PumpkinSeeds() {
        this(0, 1);
    }

    public PumpkinSeeds(int meta) {
        this(meta, 1);
    }

    public PumpkinSeeds(int meta, int count) {
        super(PUMPKIN_SEEDS, 0, count, "Pumpkin Seeds");
        this.block = Block.get(Item.PUMPKIN_STEM);
    }
}
