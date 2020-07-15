package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockDoubleSlabCrimson extends BlockSolid {

    public BlockDoubleSlabCrimson() {
    }

    @Override
    public int getId() {
        return CRIMSON_DOUBLE_SLAB;
    }
    
    @Override
    public String getName() {
        return "Double Crimson Slab";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    /*@Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }*/
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }

}
