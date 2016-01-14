package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class SandstoneStairs extends Stair {
    public SandstoneStairs() {
        this(0);
    }

    public SandstoneStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SANDSTONE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 4;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Sandstone Stairs";
    }

    @Override
    public Color getColor() {
        return Color.sandColor;
    }
}
