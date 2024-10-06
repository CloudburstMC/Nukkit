package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemItemFrameGlow extends Item {

    public ItemItemFrameGlow() {
        this(0, 1);
    }

    public ItemItemFrameGlow(Integer meta) {
        this(meta, 1);
    }

    public ItemItemFrameGlow(Integer meta, int count) {
        super(GLOW_ITEM_FRAME, meta, count, "Glow Item Frame");
        this.block = Block.get(BlockID.GLOW_FRAME);
    }
}