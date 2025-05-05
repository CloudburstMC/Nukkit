package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorMangrove extends BlockTrapdoor {

    public BlockTrapdoorMangrove() {
        this(0);
    }

    public BlockTrapdoorMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Trapdoor";
    }

    @Override
    public int getId() {
        return MANGROVE_TRAPDOOR;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
