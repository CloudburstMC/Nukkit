package cn.nukkit.block;

public class BlockCopperGrateWaxed extends BlockCopperGrate {

    public BlockCopperGrateWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Copper Grate";
    }

    @Override
    public int getId() {
        return WAXED_COPPER_GRATE;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
