package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockSmoothStone extends BlockSolid {

    @Override
    public String getName() {
        return "Smooth Stone";
    }

    @Override
    public int getId() {
        return SMOOTH_STONE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
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
