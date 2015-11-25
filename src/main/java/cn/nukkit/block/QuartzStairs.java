package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class QuartzStairs extends Stair {
    public QuartzStairs() {
        this(0);
    }

    public QuartzStairs(int meta) {
        super(QUARTZ_STAIRS, meta);
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Quartz Stairs";
    }
}
