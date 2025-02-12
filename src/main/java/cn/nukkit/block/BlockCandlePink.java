package cn.nukkit.block;

public class BlockCandlePink extends BlockCandle {

    public BlockCandlePink() {
        this(0);
    }

    public BlockCandlePink(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pink Candle";
    }

    @Override
    public int getId() {
        return PINK_CANDLE;
    }
}
