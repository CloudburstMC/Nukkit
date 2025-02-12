package cn.nukkit.block;

public class BlockCandleCakeLime extends BlockCandleCake {

    public BlockCandleCakeLime() {
        this(0);
    }

    public BlockCandleCakeLime(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Lime Candle Cake";
    }

    @Override
    public int getId() {
        return LIME_CANDLE_CAKE;
    }
}
