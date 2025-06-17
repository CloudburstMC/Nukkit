package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooTrapdoor extends BlockTrapdoor {

    public BlockBambooTrapdoor() {
        this(0);
    }

    public BlockBambooTrapdoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Trapdoor";
    }

    @Override
    public int getId() {
        return BAMBOO_TRAPDOOR;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
