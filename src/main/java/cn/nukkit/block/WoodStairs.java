package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class WoodStairs extends Stair {
    public WoodStairs() {
        this(0);
    }

    public WoodStairs(int meta) {
        super(WOOD_STAIRS, meta);
    }

    @Override
    public String getName() {
        return "Wood Stairs";
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{this.id, 0, 1}};
    }

    @Override
    public double getHardness() {
        return 2;
    }
}
