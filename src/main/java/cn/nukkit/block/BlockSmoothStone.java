package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockSmoothStone extends BlockSolid {
    
    public BlockSmoothStone() {
    }
    
    @Override
    public int getId() {
        return SMOOTH_STONE;
    }
    
    @Override
    public String getName() {
        return "Smooth Stone";
    }
    
    @Override
    public double getHardness() {
        return 1.5;
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
