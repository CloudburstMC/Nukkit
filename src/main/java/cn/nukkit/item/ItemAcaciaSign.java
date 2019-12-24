package cn.nukkit.item;


import cn.nukkit.block.BlockAcaciaSignPost;

public class ItemAcaciaSign extends ItemSign {
    public ItemAcaciaSign() {
        this(0, 1);
    }

    public ItemAcaciaSign(Integer meta) {
        this(meta, 1);
    }

    public ItemAcaciaSign(Integer meta, int count) {
        super(ACACIA_SIGN, meta, count, "Acacia Sign", new BlockAcaciaSignPost());
    }
}
