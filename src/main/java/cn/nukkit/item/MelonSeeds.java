package cn.nukkit.item;

import cn.nukkit.block.BlockStemMelon;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MelonSeeds extends Item {

    public MelonSeeds() {
        this(0, 1);
    }

    public MelonSeeds(Integer meta) {
        this(meta, 1);
    }

    public MelonSeeds(Integer meta, int count) {
        super(MELON_SEEDS, 0, count, "Melon Seeds");
        this.block = new BlockStemMelon();
    }
}
