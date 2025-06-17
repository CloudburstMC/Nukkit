package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooDoubleSlab extends BlockDoubleSlabWood {

    public BlockBambooDoubleSlab() {
        this(0);
    }

    public BlockBambooDoubleSlab(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Double Slab";
    }

    @Override
    public int getId() {
        return BAMBOO_DOUBLE_SLAB;
    }

    @Override
    public int getSingleSlabId() {
        return BAMBOO_SLAB;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
