package cn.nukkit.block;

public class BlockCopperGrateExposedWaxed extends BlockCopperGrateExposed {

    public BlockCopperGrateExposedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Grate";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_GRATE;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
