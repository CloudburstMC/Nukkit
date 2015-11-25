package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class JungleWoodStairs extends Stair {

    public JungleWoodStairs() {
        this(0);
    }

    public JungleWoodStairs(int meta) {
        super(JUNGLE_WOOD_STAIRS, meta);
    }

    @Override
    public String getName() {
        return "Jungle Wood Stairs";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{this.id, 0, 1}};
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
