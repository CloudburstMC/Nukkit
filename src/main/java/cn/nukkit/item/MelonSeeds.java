package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MelonSeeds extends Item {

    public MelonSeeds() {
        this(0, 1);
    }

    public MelonSeeds(int meta) {
        this(meta, 1);
    }

    public MelonSeeds(int meta, int count) {
        super(MELON_SEEDS, 0, count, "Melon Seeds");
        this.block = Block.get(Item.MELON_STEM);
    }
}
