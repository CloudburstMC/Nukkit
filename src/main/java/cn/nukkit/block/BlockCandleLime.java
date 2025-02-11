package cn.nukkit.block;

public class BlockCandleLime extends BlockCandle {

    public BlockCandleLime() {
        this(0);
    }

    public BlockCandleLime(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Lime Candle";
    }

    @Override
    public int getId() {
        return LIME_CANDLE;
    }
}
