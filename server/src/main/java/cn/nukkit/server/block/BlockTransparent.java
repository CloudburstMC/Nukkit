package cn.nukkit.server.block;

import cn.nukkit.server.util.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockTransparent extends Block {

    protected BlockTransparent(int meta) {
        super(meta);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

}
