package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodStrippedBirch extends BlockWoodStripped {

    public BlockWoodStrippedBirch() {
        this(0);
    }

    public BlockWoodStrippedBirch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Stripped Birch Log";
    }

    @Override
    public int getId() {
        return STRIPPED_BIRCH_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
