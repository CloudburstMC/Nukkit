package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabMangrove extends BlockDoubleSlabWood {

    public BlockDoubleSlabMangrove() {
        this(0);
    }

    public BlockDoubleSlabMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Double Slab";
    }

    @Override
    public int getId() {
        return MANGROVE_DOUBLE_SLAB;
    }

    @Override
    public int getSingleSlabId() {
        return MANGROVE_SLAB;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
