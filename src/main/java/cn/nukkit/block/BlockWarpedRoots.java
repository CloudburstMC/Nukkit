package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWarpedRoots extends BlockRoots {

    public BlockWarpedRoots() {
        this(0);
    }

    public BlockWarpedRoots(int meta) {
        super(meta);
    }

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
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
