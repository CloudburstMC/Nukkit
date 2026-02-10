package cn.nukkit.block;

public class BlockCopperChiseledWaxed extends BlockCopperChiseled {

    public BlockCopperChiseledWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Chiseled Copper";
    }

    @Override
    public int getId() {
        return WAXED_CHISELED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
