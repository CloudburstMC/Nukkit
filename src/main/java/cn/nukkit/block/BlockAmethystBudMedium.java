package cn.nukkit.block;

public class BlockAmethystBudMedium extends BlockAmethystBud {

    public BlockAmethystBudMedium() {
        this(0);
    }

    public BlockAmethystBudMedium(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MEDIUM_AMETHYST_BUD;
    }

    @Override
    protected String getSizeName() {
        return "Medium";
    }

    @Override
    protected int getCrystalHeight() {
        return 4;
    }

    @Override
    protected int getCrystalOffset() {
        return 3;
    }

    @Override
    public int getLightLevel() {
        return 2;
    }
}
