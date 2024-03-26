package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockCalcite extends BlockSolid {

    public BlockCalcite() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Calcite";
    }

    @Override
    public int getId() {
        return CALCITE;
    }

    @Override
    public double getHardness() {
        return 0.75;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    // TODO:
    /*@Override
    public boolean isLavaResistant() {
        return true;
    }*/
}
