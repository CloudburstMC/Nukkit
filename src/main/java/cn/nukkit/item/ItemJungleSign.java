package cn.nukkit.item;


import cn.nukkit.block.BlockJungleSignPost;

public class ItemJungleSign extends ItemSign {
    public ItemJungleSign() {
        this(0, 1);
    }

    public ItemJungleSign(Integer meta) {
        this(meta, 1);
    }

    public ItemJungleSign(Integer meta, int count) {
        super(JUNGLE_SIGN, meta, count, "Jungle Sign", new BlockJungleSignPost());
    }
}
