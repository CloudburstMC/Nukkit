package cn.nukkit.block;

public class BlockCandleYellow extends BlockCandle {

    public BlockCandleYellow() {
        this(0);
    }

    public BlockCandleYellow(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Yellow Candle";
    }

    @Override
    public int getId() {
        return YELLOW_CANDLE;
    }
}
