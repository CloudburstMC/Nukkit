package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockSlabStone4 extends BlockSlabStone {

    public static final int MOSSY_STONE_BRICKS = 0;
    public static final int SMOOTH_QUARTZ = 1;
    public static final int STONE = 2;
    public static final int CUT_SANDSTONE = 3;
    public static final int CUT_RED_SANDSTONE = 4;

    private static final String[] NAMES = {
            "Mossy Stone Brick",
            "Smooth Quartz",
            "Stone",
            "Cut Sandstone",
            "Cut Red Sandstone"
    };

    public BlockSlabStone4() {
        this(0);
    }

    public BlockSlabStone4(int meta) {
        super(meta, DOUBLE_STONE_SLAB4);
    }

    @Override
    public String getName() {
        int variant = this.getDamage() & 0x07;
        String name = variant >= NAMES.length ? NAMES[0] : NAMES[variant];
        return ((this.getDamage() & 0x08) > 0 ? "Upper " : "") + name + " Slab";
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            default:
            case MOSSY_STONE_BRICKS:
            case STONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case SMOOTH_QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case CUT_SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case CUT_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
        }
    }

    @Override
    public int getId() {
        return STONE_SLAB4;
    }

    @Override
    public Item toItem() {
        int damage = this.getDamage() & 0x07;
        return new ItemBlock(Block.get(this.getId(), damage), damage);
    }
}
