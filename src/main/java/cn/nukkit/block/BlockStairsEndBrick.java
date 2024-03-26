package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockStairsEndBrick extends BlockStairs {

    public BlockStairsEndBrick() {
        this(0);
    }

    public BlockStairsEndBrick(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "End Brick Stairs";
    }

    @Override
    public int getId() {
        return END_BRICK_STAIRS;
    }

    @Override
    public double getHardness() {
        return 2; //3
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
