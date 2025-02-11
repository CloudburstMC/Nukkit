package cn.nukkit.block;

public class BlockCandleCakePink extends BlockCandleCake {

    public BlockCandleCakePink() {
        this(0);
    }

    public BlockCandleCakePink(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pink Candle Cake";
    }

    @Override
    public int getId() {
        return PINK_CANDLE_CAKE;
    }
}
