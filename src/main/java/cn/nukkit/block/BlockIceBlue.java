package cn.nukkit.block;

public class BlockIceBlue extends BlockIcePacked {

    public BlockIceBlue() {

    }

    @Override
    public int getId() {
        return BLUE_ICE;
    }

    @Override
    public String getName() {
        return "Blue Ice";
    }

    @Override
    public double getResistance() {
        return 14d;
    }

    @Override
    public double getHardness() {
        return 2.8d;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98d; //TODO: check
    }
}
