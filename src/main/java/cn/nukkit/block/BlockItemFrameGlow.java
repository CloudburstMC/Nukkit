package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockItemFrameGlow extends BlockItemFrame {

    public BlockItemFrameGlow() {
        this(0);
    }

    public BlockItemFrameGlow(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Glow Item Frame";
    }

    @Override
    public int getId() {
        return GLOW_FRAME;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.GLOW_ITEM_FRAME);
    }
}
