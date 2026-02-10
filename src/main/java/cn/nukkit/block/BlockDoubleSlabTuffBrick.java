package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabTuffBrick extends BlockDoubleSlabBase {

    public BlockDoubleSlabTuffBrick() {
        this(0);
    }

    public BlockDoubleSlabTuffBrick(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return TUFF_BRICK_DOUBLE_SLAB;
    }
    
    @Override
    public int getSingleSlabId() {
        return TUFF_BRICK_SLAB;
    }

    @Override
    public int getItemDamage() {
        return 0;
    }

    @Override
    public String getSlabName() {
        return "Tuff Brick Slab";
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
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
