package cn.nukkit.block;

public class BlockCandleRed extends BlockCandle {

    public BlockCandleRed() {
        this(0);
    }

    public BlockCandleRed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Red Candle";
    }

    @Override
    public int getId() {
        return RED_CANDLE;
    }
}
