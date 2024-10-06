package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public abstract class BlockSolidMeta extends BlockMeta {

    protected BlockSolidMeta(int meta) {
        super(meta);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
