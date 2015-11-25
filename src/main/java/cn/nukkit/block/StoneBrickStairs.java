package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class StoneBrickStairs extends Stair {
    public StoneBrickStairs() {
        this(0);
    }

    public StoneBrickStairs(int meta) {
        super(STONE_BRICK_STAIRS, meta);
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public String getName() {
        return "Stone Brick Stairs";
    }
}
