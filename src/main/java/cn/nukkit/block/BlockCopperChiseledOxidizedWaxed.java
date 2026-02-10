package cn.nukkit.block;

public class BlockCopperChiseledOxidizedWaxed extends BlockCopperChiseledOxidized {

    public BlockCopperChiseledOxidizedWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Chiseled Copper";
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CHISELED_COPPER;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
