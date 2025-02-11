package cn.nukkit.block;

public class BlockCandleLightBlue extends BlockCandle {

    public BlockCandleLightBlue() {
        this(0);
    }

    public BlockCandleLightBlue(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Light Blue Candle";
    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CANDLE;
    }
}
