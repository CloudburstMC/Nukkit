package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Iron extends Solid {


    public Iron() {
        this(0);
    }

    public Iron(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return IRON_BLOCK;
    }

    @Override
    public String getName() {
        return "Iron Block";
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_STONE) {
            return new int[][]{new int[]{Item.IRON_BLOCK, 0, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public Color getColor() {
        return Color.ironColor;
    }
}
