package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * @author good777LUCKY
 */
public class BlockStructureVoid extends BlockSolid {

    public BlockStructureVoid() {
        // Does Nothing
    }
    
    @Override
    public int getId() {
        return STRUCTURE_VOID;
    }
    
    @Override
    public String getName() {
        return "Structure Void";
    }
    
    @Override
    public double getHardness() {
        return 0;
    }
    
    @Override
    public double getResistance() {
        return 0;
    }
    
    @Override
    public boolean canPassThrough() {
        return true;
    }
    
    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
    
    @Override
    public boolean isBreakable(Item item) {
        return false;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBePulled() {
        return false;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }
}
