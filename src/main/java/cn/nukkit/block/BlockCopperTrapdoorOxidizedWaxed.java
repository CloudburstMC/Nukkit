package cn.nukkit.block;

public class BlockCopperTrapdoorOxidizedWaxed extends BlockCopperTrapdoorOxidized {

    public BlockCopperTrapdoorOxidizedWaxed() {
        this(0);
    }

    public BlockCopperTrapdoorOxidizedWaxed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Trapdoor";
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_TRAPDOOR;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
