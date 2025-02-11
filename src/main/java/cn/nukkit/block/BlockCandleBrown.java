package cn.nukkit.block;

public class BlockCandleBrown extends BlockCandle {

    public BlockCandleBrown() {
        this(0);
    }

    public BlockCandleBrown(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Brown Candle";
    }

    @Override
    public int getId() {
        return BROWN_CANDLE;
    }
}
