package cn.nukkit.item;

import cn.nukkit.block.BlockBeetroot;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BeetrootSeeds extends Item {

    public BeetrootSeeds() {
        this(0, 1);
    }

    public BeetrootSeeds(Integer meta) {
        this(meta, 1);
    }

    public BeetrootSeeds(Integer meta, int count) {
        super(BEETROOT_SEEDS, 0, count, "Beetroot Seeds");
        this.block = new BlockBeetroot();
    }
}
