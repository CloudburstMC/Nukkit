package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Potato extends Item {

    public Potato() {
        this(0, 1);
    }

    public Potato(int meta) {
        this(meta, 1);
    }

    public Potato(int meta, int count) {
        super(POTATO, 0, count, "Potato");
        this.block = Block.get(Item.POTATO_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
