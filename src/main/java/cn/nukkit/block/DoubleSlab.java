package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.RGBColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DoubleSlab extends Solid {
    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOODEN = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;

    public DoubleSlab() {
        this(0);
    }

    public DoubleSlab(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_SLAB;
    }

    //todo hardness and residence

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Stone",
                "Sandstone",
                "Wooden",
                "Cobblestone",
                "Brick",
                "Stone Brick",
                "Quartz",
                "Nether Brick"
        };
        return "Double " + names[this.meta & 0x07] + " Slab";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.SLAB, this.meta & 0x07, 2}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public RGBColor getMapColor() {
        switch (this.meta & 0x07) {
            case DoubleSlab.STONE: return RGBColor.stoneColor;
            case DoubleSlab.SANDSTONE: return RGBColor.sandColor;
            case DoubleSlab.WOODEN: return RGBColor.woodColor;
            case DoubleSlab.COBBLESTONE: return RGBColor.stoneColor;
            case DoubleSlab.BRICK: return RGBColor.stoneColor;
            case DoubleSlab.STONE_BRICK: return RGBColor.stoneColor;
            case DoubleSlab.QUARTZ: return RGBColor.quartzColor;
            case DoubleSlab.NETHER_BRICK: return RGBColor.netherrackColor;

            default: return RGBColor.stoneColor;     //unreachable
        }
    }
}
