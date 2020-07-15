package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockCrimsonPlanks extends BlockSolid {

    public BlockCrimsonPlanks() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRIMSON_PLANKS;
    }
    
    @Override
    public String getName() {
        return "Crimson Planks";
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
