package cn.nukkit.block;

public class BlockCopperChiseledExposedWaxed extends BlockCopperChiseledExposed {

    public BlockCopperChiseledExposedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Exposed Chiseled Copper";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_CHISELED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
