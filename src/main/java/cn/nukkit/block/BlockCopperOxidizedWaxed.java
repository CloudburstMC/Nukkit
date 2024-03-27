package cn.nukkit.block;

public class BlockCopperOxidizedWaxed extends BlockCopperOxidized {

    public BlockCopperOxidizedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper";
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
