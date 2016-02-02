package cn.nukkit.item;

import cn.nukkit.block.SignPost;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Sign extends Item {

    public Sign() {
        this(0, 1);
    }

    public Sign(Integer meta) {
        this(meta, 1);
    }

    public Sign(Integer meta, int count) {
        super(SIGN, 0, count, "Sign");
        this.block = new SignPost();
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
