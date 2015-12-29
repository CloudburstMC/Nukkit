package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AcaciaWoodStairs extends Stair {

    public AcaciaWoodStairs() {
        this(0);
    }

    public AcaciaWoodStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Acacia Wood Stairs";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{this.getId(), 0, 1}};
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
}
