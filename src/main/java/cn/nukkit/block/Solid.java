package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Solid extends Block {

    protected Solid(int meta) {
        super(meta);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
