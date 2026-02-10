package cn.nukkit.block;

public class BlockCopperBulbOxidizedWaxed extends BlockCopperBulbOxidized {

    public BlockCopperBulbOxidizedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Bulb";
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_BULB;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
