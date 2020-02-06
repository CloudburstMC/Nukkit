package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockTrapdooDarkOak extends BlockTrapdoor {

    public BlockTrapdooDarkOak(Identifier identifier) {
        super(identifier);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
