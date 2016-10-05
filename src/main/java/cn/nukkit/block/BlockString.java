package cn.nukkit.block;

public class BlockString extends BlockTransparent {

    public BlockString() {
        this(0);
    }

    public BlockString(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TRIPWIRE;
    }

    @Override
    public String getName() {
        return "Tripwire";
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public double getHardness() {
        return 0;
    }
}
