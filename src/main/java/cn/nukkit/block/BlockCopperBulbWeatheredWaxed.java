package cn.nukkit.block;

public class BlockCopperBulbWeatheredWaxed extends BlockCopperBulbWeathered {

    public BlockCopperBulbWeatheredWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Bulb";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_BULB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
