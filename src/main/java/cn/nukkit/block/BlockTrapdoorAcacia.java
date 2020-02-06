package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockTrapdoorAcacia extends BlockTrapdoor {

    public BlockTrapdoorAcacia(Identifier identifier) {
        super(identifier);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
