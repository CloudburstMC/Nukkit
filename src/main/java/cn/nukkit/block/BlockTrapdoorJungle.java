package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockTrapdoorJungle extends BlockTrapdoor {

    public BlockTrapdoorJungle(Identifier identifier) {
        super(identifier);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
