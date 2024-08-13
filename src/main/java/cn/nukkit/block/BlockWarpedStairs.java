package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWarpedStairs extends BlockStairsWood {

    public BlockWarpedStairs() {
        this(0);
    }

    public BlockWarpedStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_STAIRS;
    }

    @Override
    public String getName() {
        return "Warped Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
