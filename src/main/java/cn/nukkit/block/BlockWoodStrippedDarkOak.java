package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodStrippedDarkOak extends BlockWoodStripped {

    public BlockWoodStrippedDarkOak() {
        this(0);
    }

    public BlockWoodStrippedDarkOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Stripped Dark Oak Log";
    }

    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
