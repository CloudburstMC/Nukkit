package cn.nukkit.block;

public class BlockCandleMagenta extends BlockCandle {

    public BlockCandleMagenta() {
        this(0);
    }

    public BlockCandleMagenta(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Magenta Candle";
    }

    @Override
    public int getId() {
        return MAGENTA_CANDLE;
    }
}
