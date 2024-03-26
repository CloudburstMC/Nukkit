package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockStairsAndesite extends BlockStairs {

    public BlockStairsAndesite() {
        this(0);
    }

    public BlockStairsAndesite(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Andesite Stairs";
    }

    @Override
    public int getId() {
        return ANDESITE_STAIRS;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }
}
