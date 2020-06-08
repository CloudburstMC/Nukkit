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
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{toItem()};
        } else {
            return new Item[0];
        }
    }
}
