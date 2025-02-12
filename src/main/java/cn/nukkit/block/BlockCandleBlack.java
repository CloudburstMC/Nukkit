package cn.nukkit.block;

public class BlockCandleBlack extends BlockCandle {

    public BlockCandleBlack() {
        this(0);
    }

    public BlockCandleBlack(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Black Candle";
    }

    @Override
    public int getId() {
        return BLACK_CANDLE;
    }
}
