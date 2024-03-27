package cn.nukkit.block;

public class BlockDoubleSlabCopperCutWaxed extends BlockDoubleSlabCopperCut {
    
    public BlockDoubleSlabCopperCutWaxed() {
        this(0);
    }
    
    public BlockDoubleSlabCopperCutWaxed(int meta) {
        super(meta);
    }
    
    @Override
    public int getSingleSlabId() {
        return WAXED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WAXED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
