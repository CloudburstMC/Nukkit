package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockWarpedPlanks extends BlockSolid {

    public BlockWarpedPlanks() {
        // Does nothing
    }

    @Override
    public int getId() {
        return WARPED_PLANKS;
    }
    
    @Override
    public String getName() {
        return "Warped Planks";
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
