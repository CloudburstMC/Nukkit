package cn.nukkit.block;

public class BlockCopperBulbWaxed extends BlockCopperBulb {

    public BlockCopperBulbWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Copper Bulb";
    }

    @Override
    public int getId() {
        return WAXED_COPPER_BULB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
