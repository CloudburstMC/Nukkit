package cn.nukkit.block;

public class BlockCandleCakeMagenta extends BlockCandleCake {

    public BlockCandleCakeMagenta() {
        this(0);
    }

    public BlockCandleCakeMagenta(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Magenta Candle Cake";
    }

    @Override
    public int getId() {
        return MAGENTA_CANDLE_CAKE;
    }
}
