package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockTrapdoorBirch extends BlockTrapdoor {

    public BlockTrapdoorBirch(Identifier identifier) {
        super(identifier);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
