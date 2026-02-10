package cn.nukkit.block;

public class BlockCopperChiseledWeatheredWaxed extends BlockCopperChiseledWeathered {

    public BlockCopperChiseledWeatheredWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Weathered Chiseled Copper";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CHISELED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
