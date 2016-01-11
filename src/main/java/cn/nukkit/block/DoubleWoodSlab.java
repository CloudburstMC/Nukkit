package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class DoubleWoodSlab extends Solid {

    public DoubleWoodSlab() {
        this(0);
    }

    public DoubleWoodSlab(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_WOOD_SLAB;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak",
                "Spruce",
                "Birch",
                "Jungle",
                "Acacia",
                "Dark Oak",
                "",
                ""
        };
        return "Double " + names[this.meta & 0x07] + " Slab";
    }

    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.WOOD_SLAB, this.meta & 0x07, 2}
        };
    }
}
