package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockTrapdoorSpruce extends BlockTrapdoor {

    public BlockTrapdoorSpruce(Identifier identifier) {
        super(identifier);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
