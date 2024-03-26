package cn.nukkit.block;

public class BlockDoubleSlabCopperCutWeatheredWaxed extends BlockDoubleSlabCopperCutWeathered {
    
    public BlockDoubleSlabCopperCutWeatheredWaxed() {
        this(0);
    }
    
    public BlockDoubleSlabCopperCutWeatheredWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getSingleSlabId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
