package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class NetherBrickStairs extends Stair {
    public NetherBrickStairs() {
        this(0);
    }

    public NetherBrickStairs(int meta) {
        super(NETHER_BRICKS_STAIRS, meta);
    }

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
        return "Nether Bricks Stairs";
    }
}
