package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockRootsWarped extends BlockRoots {
    @Override
    public int getId() {
        return WARPED_ROOTS;
    }

    @Override
    public String getName() {
        return "Warped Roots";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
