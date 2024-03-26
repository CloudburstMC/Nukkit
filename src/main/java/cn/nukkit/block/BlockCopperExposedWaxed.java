package cn.nukkit.block;

public class BlockCopperExposedWaxed extends BlockCopperExposed {

    public BlockCopperExposedWaxed( ) {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
