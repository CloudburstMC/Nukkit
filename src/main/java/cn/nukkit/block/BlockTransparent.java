package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockTransparent extends Block {

    public BlockTransparent(Identifier id) {
        super(id);
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
