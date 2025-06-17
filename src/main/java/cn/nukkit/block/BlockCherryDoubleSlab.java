package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCherryDoubleSlab extends BlockDoubleSlabWood {

    public BlockCherryDoubleSlab() {
        this(0);
    }

    public BlockCherryDoubleSlab(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Double Slab";
    }

    @Override
    public int getId() {
        return CHERRY_DOUBLE_SLAB;
    }

    @Override
    public int getSingleSlabId() {
        return CHERRY_SLAB;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
