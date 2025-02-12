package cn.nukkit.block;

public class BlockCandlePurple extends BlockCandle {

    public BlockCandlePurple() {
        this(0);
    }

    public BlockCandlePurple(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Purple Candle";
    }

    @Override
    public int getId() {
        return PURPLE_CANDLE;
    }
}
