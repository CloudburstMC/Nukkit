package cn.nukkit.item;


import cn.nukkit.block.BlockWarpedSignPost;

public class ItemWarpedSign extends ItemSign {
    public ItemWarpedSign() {
        this(0, 1);
    }

    public ItemWarpedSign(Integer meta) {
        this(meta, 1);
    }

    public ItemWarpedSign(Integer meta, int count) {
        super(WARPED_SIGN, meta, count, "Warped Sign", new BlockWarpedSignPost());
    }
}
