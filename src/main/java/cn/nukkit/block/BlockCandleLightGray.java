package cn.nukkit.block;

public class BlockCandleLightGray extends BlockCandle {

    public BlockCandleLightGray() {
        this(0);
    }

    public BlockCandleLightGray(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Light Gray Candle";
    }

    @Override
    public int getId() {
        return LIGHT_GRAY_CANDLE;
    }
}
