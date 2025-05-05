package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockAmethyst extends BlockSolid {

    public BlockAmethyst() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Amethyst Block";
    }

    @Override
    public int getId() {
        return AMETHYST_BLOCK;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 1.5;
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
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    // TODO:
    /*@Override
    public boolean isLavaResistant() {
        return true;
    }*/
}
