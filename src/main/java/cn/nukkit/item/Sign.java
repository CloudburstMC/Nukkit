package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Sign extends Item {

    public Sign() {
        this(0, 1);
    }

    public Sign(int meta) {
        this(meta, 1);
    }

    public Sign(int meta, int count) {
        super(SIGN, 0, count, "Sign");
        this.block = Block.get(Item.SIGN_POST);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
