package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCrimsonRoots extends BlockRoots {

    public BlockCrimsonRoots() {
        this(0);
    }

    public BlockCrimsonRoots(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Roots";
    }

    @Override
    public int getId() {
        return CRIMSON_ROOTS;
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
