package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Potato extends EdibleItem {

    public Potato() {
        this(0, 1);
    }

    public Potato(Integer meta) {
        this(meta, 1);
    }

    public Potato(Integer meta, int count) {
        super(POTATO, 0, count, "Potato");
        this.block = Block.get(Item.POTATO_BLOCK);
    }
}
