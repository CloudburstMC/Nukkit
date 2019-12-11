package cn.nukkit.block;

public class BlockHoneyComb extends BlockSolid {

    @Override
    public String getName() {
        return "Honeycomb Block";
    }

    @Override
    public int getId() {
        return HONEYCOMB_BLOCK;
    }

    @Override
    public double getHardness() { return 0.6; }
}
