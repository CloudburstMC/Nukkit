package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooStairs extends BlockStairsWood {

    public BlockBambooStairs() {
        this(0);
    }

    public BlockBambooStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_STAIRS;
    }

    @Override
    public String getName() {
        return "Bamboo Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
