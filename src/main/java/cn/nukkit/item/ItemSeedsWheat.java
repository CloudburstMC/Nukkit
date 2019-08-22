package cn.nukkit.item;

import cn.nukkit.block.BlockWheat;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSeedsWheat extends Item {

    public ItemSeedsWheat() {
        this(0, 1);
    }

    public ItemSeedsWheat(Integer meta) {
        this(meta, 1);
    }

    public ItemSeedsWheat(Integer meta, int count) {
        super(WHEAT_SEEDS, 0, count, "Seeds");
        this.block = new BlockWheat();
    }
}
