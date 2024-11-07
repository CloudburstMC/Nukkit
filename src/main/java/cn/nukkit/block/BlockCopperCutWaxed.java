package cn.nukkit.block;

public class BlockCopperCutWaxed extends BlockCopperCut {

    public BlockCopperCutWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Cut Copper";
    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
