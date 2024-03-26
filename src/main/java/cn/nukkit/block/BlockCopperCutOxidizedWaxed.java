package cn.nukkit.block;

public class BlockCopperCutOxidizedWaxed extends BlockCopperCutOxidized {

    public BlockCopperCutOxidizedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Cut Copper";
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CUT_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
