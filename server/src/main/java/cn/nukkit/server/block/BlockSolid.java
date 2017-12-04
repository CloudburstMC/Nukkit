package cn.nukkit.server.block;

import cn.nukkit.server.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSolid extends Block {

    protected BlockSolid(int meta) {
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
