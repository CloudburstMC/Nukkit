package cn.nukkit.block;

public class BlockCandleBlue extends BlockCandle {

    public BlockCandleBlue() {
        this(0);
    }

    public BlockCandleBlue(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blue Candle";
    }

    @Override
    public int getId() {
        return BLUE_CANDLE;
    }
}
