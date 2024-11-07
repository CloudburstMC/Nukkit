package cn.nukkit.block;

public class BlockBlueIce extends BlockIcePacked {

    @Override
    public String getName() {
        return "Blue Ice";
    }

    @Override
    public int getId() {
        return BLUE_ICE;
    }

    @Override
    public double getFrictionFactor() {
        return 0.989;
    }

    @Override
    public double getHardness() {
        return 2.8;
    }

    @Override
    public double getResistance() {
        return 14;
    }
}
