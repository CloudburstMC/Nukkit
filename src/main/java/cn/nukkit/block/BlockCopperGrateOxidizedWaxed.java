package cn.nukkit.block;

public class BlockCopperGrateOxidizedWaxed extends BlockCopperGrateOxidized {

    public BlockCopperGrateOxidizedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Grate";
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_GRATE;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
