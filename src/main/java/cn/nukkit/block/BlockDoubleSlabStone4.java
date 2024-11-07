package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabStone4 extends BlockDoubleSlabBase {

    private static final String[] NAMES = {
            "Mossy Stone Brick",
            "Smooth Quartz",
            "Stone",
            "Cut Sandstone",
            "Cut Red Sandstone",
    };

    public static final int MOSSY_STONE_BRICKS = 0;
    public static final int SMOOTH_QUARTZ = 1;
    public static final int STONE = 2;
    public static final int CUT_SANDSTONE = 3;
    public static final int CUT_RED_SANDSTONE = 4;

    public BlockDoubleSlabStone4() {
        this(0);
    }

    public BlockDoubleSlabStone4(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB4;
    }

    @Override
    public int getSingleSlabId() {
        return STONE_SLAB4;
    }

    @Override
    public double getResistance() {
        return this.getToolType() > ItemTool.TIER_WOODEN ? 30 : 15;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getSlabName() {
        int variant = this.getDamage() & 0x07;
        if (variant >= NAMES.length) {
            return NAMES[0];
        }
        return NAMES[variant];
    }

    @Override
    public int getItemDamage() {
        return this.getDamage() & 0x07;
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
    public boolean canHarvestWithHand() {
        return false;
    }
}