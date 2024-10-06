package cn.nukkit.block;

public class BlockAmethystBudLarge extends BlockAmethystBud {

    public BlockAmethystBudLarge() {
        this(0);
    }

    public BlockAmethystBudLarge(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LARGE_AMETHYST_BUD;
    }

    @Override
    protected String getSizeName() {
        return "Large";
    }

    @Override
    protected int getCrystalHeight() {
        return 5;
    }

    @Override
    protected int getCrystalOffset() {
        return 3;
    }

    @Override
    public int getLightLevel() {
        return 4;
    }
}
