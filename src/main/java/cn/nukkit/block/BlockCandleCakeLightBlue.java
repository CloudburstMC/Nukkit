package cn.nukkit.block;

public class BlockCandleCakeLightBlue extends BlockCandleCake {

    public BlockCandleCakeLightBlue() {
        this(0);
    }

    public BlockCandleCakeLightBlue(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Light Blue Candle Cake";
    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CANDLE_CAKE;
    }
}
