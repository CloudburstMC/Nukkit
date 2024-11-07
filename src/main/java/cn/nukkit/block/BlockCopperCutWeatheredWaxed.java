package cn.nukkit.block;

public class BlockCopperCutWeatheredWaxed extends BlockCopperCutWeathered {

    public BlockCopperCutWeatheredWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Weathered Cut Copper";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CUT_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
