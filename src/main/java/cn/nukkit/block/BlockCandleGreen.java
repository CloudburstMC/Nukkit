package cn.nukkit.block;

public class BlockCandleGreen extends BlockCandle {

    public BlockCandleGreen() {
        this(0);
    }

    public BlockCandleGreen(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Green Candle";
    }

    @Override
    public int getId() {
        return GREEN_CANDLE;
    }
}
