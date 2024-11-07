package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCrimsonStairs extends BlockStairsWood {

    public BlockCrimsonStairs() {
        this(0);
    }

    public BlockCrimsonStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_STAIRS;
    }

    @Override
    public String getName() {
        return "Crimson Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
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