package cn.nukkit.block;

public class BlockCopperWaxed extends BlockCopper {

    public BlockCopperWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Block of Copper";
    }

    @Override
    public int getId() {
        return WAXED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
