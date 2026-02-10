package cn.nukkit.block;

public class BlockCopperTrapdoorWaxed extends BlockCopperTrapdoor {

    public BlockCopperTrapdoorWaxed() {
        this(0);
    }

    public BlockCopperTrapdoorWaxed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Waxed Copper Trapdoor";
    }

    @Override
    public int getId() {
        return WAXED_COPPER_TRAPDOOR;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
