package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabBrickDeepslate extends BlockDoubleSlabBase {

    public BlockDoubleSlabBrickDeepslate() {
        this(0);
    }
    
    protected BlockDoubleSlabBrickDeepslate(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DEEPSLATE_BRICK_DOUBLE_SLAB;
    }
    
    @Override
    public int getSingleSlabId() {
        return DEEPSLATE_BRICK_SLAB;
    }

    @Override
    public int getItemDamage() {
        return 0;
    }

    @Override
    public String getSlabName() {
        return "Double Deepslate Brick Slab";
    }
    
    @Override
    public double getHardness() {
        return 3.5;
    }
    
    @Override
    public double getResistance() {
        return 6;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY_BLOCK_COLOR;
    }
}
