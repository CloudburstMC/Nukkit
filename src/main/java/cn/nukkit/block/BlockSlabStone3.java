package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockSlabStone3 extends BlockSlabStone {

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

    public BlockSlabStone3() {
        this(0);
    }

    public BlockSlabStone3(int meta) {
        super(meta, DOUBLE_STONE_SLAB3);
    }

    @Override
    public String getName() {
        return ((this.getDamage() & 0x08) > 0 ? "Upper " : "") + NAMES[this.getDamage() & 0x07] + " Slab";
    }

    @Override
    public int getId() {
        return STONE_SLAB3;
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
    public Item toItem() {
        int damage = this.getDamage() & 0x07;
        return new ItemBlock(Block.get(this.getId(), damage), damage);
    }
}
