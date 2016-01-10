package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class CobblestoneStairs extends Stair {
    public CobblestoneStairs() {
        this(0);
    }

    public CobblestoneStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COBBLESTONE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Cobblestone Stairs";
    }
}
