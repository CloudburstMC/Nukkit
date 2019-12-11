package cn.nukkit.block;

public class BlockHoney extends BlockTransparent {

    @Override
    public String getName() {
        return "Honey Block";
    }

    @Override
    public int getId() {
        return HONEY_BLOCK;
    }

    @Override
    public double getHardness() { return 0; }

    @Override
    public double getResistance() { return 0;}
}
