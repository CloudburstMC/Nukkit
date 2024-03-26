package cn.nukkit.block;

public class BlockCopperCutExposedWaxed extends BlockCopperCutExposed {

    public BlockCopperCutExposedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Exposed Cut Copper";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_CUT_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
