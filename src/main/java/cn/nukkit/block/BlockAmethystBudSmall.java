package cn.nukkit.block;

public class BlockAmethystBudSmall extends BlockAmethystBud {

    public BlockAmethystBudSmall() {
        this(0);
    }

    public BlockAmethystBudSmall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SMALL_AMETHYST_BUD;
    }

    @Override
    protected String getSizeName() {
        return "Small";
    }

    @Override
    protected int getCrystalHeight() {
        return 3;
    }

    @Override
    protected int getCrystalOffset() {
        return 4;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }
}
