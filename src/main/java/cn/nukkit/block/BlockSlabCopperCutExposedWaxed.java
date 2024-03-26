package cn.nukkit.block;

public class BlockSlabCopperCutExposedWaxed extends BlockSlabCopperCutExposed {
    
    public BlockSlabCopperCutExposedWaxed() {
        this(0);
    }
    
    public BlockSlabCopperCutExposedWaxed(int meta) {
        super(meta, WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
