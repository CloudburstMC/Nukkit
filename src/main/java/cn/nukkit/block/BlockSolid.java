package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSolid extends Block {

    public BlockSolid(Identifier id) {
        super(id);
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
