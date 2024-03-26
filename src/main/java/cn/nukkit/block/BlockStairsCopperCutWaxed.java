package cn.nukkit.block;

public class BlockStairsCopperCutWaxed extends BlockStairsCopperCut {
    
    public BlockStairsCopperCutWaxed() {
        this(0);
    }

    public BlockStairsCopperCutWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER_STAIRS;
    }

    
    @Override
    public boolean isWaxed() {
        return true;
    }
}
