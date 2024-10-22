package cn.nukkit.block;

public class BlockStairsCopperCutWeatheredWaxed extends BlockStairsCopperCutWeathered {
    
    public BlockStairsCopperCutWeatheredWaxed() {
        this(0);
    }
    
    public BlockStairsCopperCutWeatheredWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CUT_COPPER_STAIRS;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
