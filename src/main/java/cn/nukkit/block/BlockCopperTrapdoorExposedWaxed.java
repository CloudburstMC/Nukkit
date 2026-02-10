package cn.nukkit.block;

public class BlockCopperTrapdoorExposedWaxed extends BlockCopperTrapdoorExposed {

    public BlockCopperTrapdoorExposedWaxed() {
        this(0);
    }

    public BlockCopperTrapdoorExposedWaxed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Trapdoor";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_TRAPDOOR;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
