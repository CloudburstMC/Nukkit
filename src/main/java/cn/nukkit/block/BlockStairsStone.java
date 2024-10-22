package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockStairsStone extends BlockStairs {

    public BlockStairsStone() {
        this(0);
    }

    public BlockStairsStone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Stone Stairs";
    }

    @Override
    public int getId() {
        return NORMAL_STONE_STAIRS;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
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
    public boolean canHarvestWithHand() {
        return false;
    }
}
