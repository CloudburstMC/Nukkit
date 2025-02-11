package cn.nukkit.block;

public class BlockCandleCakeLightGray extends BlockCandleCake {

    public BlockCandleCakeLightGray() {
        this(0);
    }

    public BlockCandleCakeLightGray(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Light Gray Candle Cake";
    }

    @Override
    public int getId() {
        return LIGHT_GRAY_CANDLE_CAKE;
    }
}
