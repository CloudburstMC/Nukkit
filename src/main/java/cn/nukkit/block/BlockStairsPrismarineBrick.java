package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsPrismarineBrick extends BlockStairsPrismarine {

    public BlockStairsPrismarineBrick() {
        this(0);
    }

    public BlockStairsPrismarineBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PRISMARINE_BRICKS_STAIRS;
    }

    @Override
    public String getName() {
        return "Prismarine Brick Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }
}
