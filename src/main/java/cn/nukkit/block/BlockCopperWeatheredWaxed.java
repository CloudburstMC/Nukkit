package cn.nukkit.block;

public class BlockCopperWeatheredWaxed extends BlockCopperWeathered {

    public BlockCopperWeatheredWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
