package cn.nukkit.block;

public class BlockCandleOrange extends BlockCandle {

    public BlockCandleOrange() {
        this(0);
    }

    public BlockCandleOrange(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Orange Candle";
    }

    @Override
    public int getId() {
        return ORANGE_CANDLE;
    }
}
