package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparentMeta extends BlockMeta {

    protected BlockTransparentMeta() {
        this(0);
    }

    protected BlockTransparentMeta(int meta) {
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
