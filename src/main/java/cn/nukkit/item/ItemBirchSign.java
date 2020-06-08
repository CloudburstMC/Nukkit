package cn.nukkit.item;

import cn.nukkit.block.BlockBirchSignPost;

public class ItemBirchSign extends ItemSign {
    public ItemBirchSign() {
        this(0, 1);
    }

    public ItemBirchSign(Integer meta) {
        this(meta, 1);
    }

    public ItemBirchSign(Integer meta, int count) {
        super(BIRCH_SIGN, meta, count, "Birch Sign", new BlockBirchSignPost());
    }
}
