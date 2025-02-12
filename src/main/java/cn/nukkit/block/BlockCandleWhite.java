package cn.nukkit.block;

public class BlockCandleWhite extends BlockCandle {

    public BlockCandleWhite() {
        this(0);
    }

    public BlockCandleWhite(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "White Candle";
    }

    @Override
    public int getId() {
        return WHITE_CANDLE;
    }
}
