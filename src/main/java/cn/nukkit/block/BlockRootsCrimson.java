package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockRootsCrimson extends BlockRoots {
    @Override
    public int getId() {
        return CRIMSON_ROOTS;
    }

    @Override
    public String getName() {
        return "Crimson Roots";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
