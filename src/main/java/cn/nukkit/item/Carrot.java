package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Carrot extends Item {

    public Carrot() {
        this(0, 1);
    }

    public Carrot(int meta) {
        this(meta, 1);
    }

    public Carrot(int meta, int count) {
        super(CARROT, 0, count, "Carrot");
        this.block = Block.get(Item.CARROT_BLOCK);
    }

}
