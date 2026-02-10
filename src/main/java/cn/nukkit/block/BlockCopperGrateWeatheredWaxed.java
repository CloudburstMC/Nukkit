package cn.nukkit.block;

public class BlockCopperGrateWeatheredWaxed extends BlockCopperGrateWeathered {

    public BlockCopperGrateWeatheredWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Grate";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_GRATE;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
