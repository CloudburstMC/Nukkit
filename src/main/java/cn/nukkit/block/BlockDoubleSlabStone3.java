package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabStone3 extends BlockDoubleSlabBase  {

    public static final int END_STONE_BRICKS = 0;
    public static final int SMOOTH_RED_SANDSTONE = 1;
    public static final int POLISHED_ANDESITE = 2;
    public static final int ANDESITE = 3;
    public static final int DIORITE = 4;
    public static final int POLISHED_DIORITE = 5;
    public static final int GRANITE = 6;
    public static final int POLISHED_GRANITE = 7;

    private static final String[] NAMES = {
            "End Stone Brick",
            "Smooth Red Sandstone",
            "Polished Andesite",
            "Andesite",
            "Diorite",
            "Polished Diorite",
            "Granite",
            "Polisehd Granite"
    };

    public BlockDoubleSlabStone3() {
        this(0);
    }

    public BlockDoubleSlabStone3(int meta) {
        super(meta);
    }

    @Override
    public String getSlabName() {
        return NAMES[this.getDamage() & 0x07];
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB3;
    }

    @Override
    public int getSingleSlabId() {
        return STONE_SLAB3;
    }

    @Override
    public int getItemDamage() {
        return this.getDamage() & 0x07;
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            case END_STONE_BRICKS:
                return BlockColor.SAND_BLOCK_COLOR;
            case SMOOTH_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            default:
            case POLISHED_ANDESITE:
            case ANDESITE:
                return BlockColor.STONE_BLOCK_COLOR;
            case DIORITE:
            case POLISHED_DIORITE:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case GRANITE:
            case POLISHED_GRANITE:
                return BlockColor.DIRT_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
