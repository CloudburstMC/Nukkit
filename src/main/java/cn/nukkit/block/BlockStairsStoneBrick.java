package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsStoneBrick extends BlockStairs {
    public BlockStairsStoneBrick() {
        this(0);
    }

    public BlockStairsStoneBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_BRICK_STAIRS;
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
    public double getResistance() {
        return 30;
    }

    @Override
    public String getName() {
        return "Stone Brick Stairs";
    }
}
