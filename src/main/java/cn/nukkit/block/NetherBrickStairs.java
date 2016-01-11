package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class NetherBrickStairs extends Stair {
    public NetherBrickStairs() {
        this(0);
    }

    public NetherBrickStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return NETHER_BRICKS_STAIRS;
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

    @Override
    public Color getMapColor() {
        return Color.netherrackColor;
    }

}
