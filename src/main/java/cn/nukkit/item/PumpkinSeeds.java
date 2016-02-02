package cn.nukkit.item;

import cn.nukkit.block.BlockStemPumpkin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PumpkinSeeds extends Item {

    public PumpkinSeeds() {
        this(0, 1);
    }

    public PumpkinSeeds(Integer meta) {
        this(meta, 1);
    }

    public PumpkinSeeds(Integer meta, int count) {
        super(PUMPKIN_SEEDS, 0, count, "Pumpkin Seeds");
        this.block = new BlockStemPumpkin();
    }
}
