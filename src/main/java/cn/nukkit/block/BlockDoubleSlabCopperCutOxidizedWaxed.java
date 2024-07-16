package cn.nukkit.block;

public class BlockDoubleSlabCopperCutOxidizedWaxed extends BlockDoubleSlabCopperCutOxidized {
    
    public BlockDoubleSlabCopperCutOxidizedWaxed() {
        this(0);
    }

    public BlockDoubleSlabCopperCutOxidizedWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getSingleSlabId() {
        return WAXED_OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
