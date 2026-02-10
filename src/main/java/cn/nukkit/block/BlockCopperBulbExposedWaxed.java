package cn.nukkit.block;

public class BlockCopperBulbExposedWaxed extends BlockCopperBulbExposed {

    public BlockCopperBulbExposedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Bulb";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_BULB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
