package cn.nukkit.item;


import cn.nukkit.block.BlockDarkOakSignPost;

public class ItemDarkOakSign extends ItemSign {
    public ItemDarkOakSign() {
        this(0, 1);
    }

    public ItemDarkOakSign(Integer meta) {
        this(meta, 1);
    }

    public ItemDarkOakSign(Integer meta, int count) {
        super(DARKOAK_SIGN, meta, count, "Dark Oak Sign", new BlockDarkOakSignPost());
    }
}
