package cn.nukkit.block;

public class BlockSnifferEgg extends BlockTransparentMeta {

    public BlockSnifferEgg() {
        this(0);
    }

    public BlockSnifferEgg(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Sniffer Egg";
    }

    @Override
    public int getId() {
        return SNIFFER_EGG;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
