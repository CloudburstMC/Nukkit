package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockDoubleSlabStone extends BlockDoubleSlabBase  {

    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOODEN = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;

    private static final String[] NAMES = {
            "Stone",
            "Sandstone",
            "Oak",
            "Cobblestone",
            "Brick",
            "Stone Brick",
            "Quartz",
            "Nether Brick"
    };

    public BlockDoubleSlabStone() {
        this(0);
    }

    public BlockDoubleSlabStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_SLAB;
    }

    @Override
    public int getSingleSlabId() {
        return STONE_SLAB;
    }

    @Override
    public double getResistance() {
        return getToolType() > ItemTool.TIER_WOODEN ? 30 : 15;
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
        return NAMES[this.getDamage() & 0x07];
    }

    @Override
    public int getItemDamage() {
        return this.getDamage() & 0x07;
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            default:
            case BlockDoubleSlabStone.STONE:
            case BlockDoubleSlabStone.COBBLESTONE:
            case BlockDoubleSlabStone.BRICK:
            case BlockDoubleSlabStone.STONE_BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockDoubleSlabStone.SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case BlockDoubleSlabStone.WOODEN:
                return BlockColor.WOOD_BLOCK_COLOR;
            case BlockDoubleSlabStone.QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case BlockDoubleSlabStone.NETHER_BRICK:
                return BlockColor.NETHERRACK_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
