package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockSoulSoil extends BlockSolid {
    @Override
    public int getId() {
        return SOUL_SOIL;
    }

    @Override
    public String getName() {
        return "Soul Soil";
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }
}
