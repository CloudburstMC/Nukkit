package cn.nukkit.item;

import cn.nukkit.block.BlockSpruceSignPost;

public class ItemSpruceSign extends ItemSign {
    public ItemSpruceSign() {
        this(0, 1);
    }

    public ItemSpruceSign(Integer meta) {
        this(meta, 1);
    }

    public ItemSpruceSign(Integer meta, int count) {
        super(SPRUCE_SIGN, meta, count, "Spruce Sign", new BlockSpruceSignPost());
    }
}
