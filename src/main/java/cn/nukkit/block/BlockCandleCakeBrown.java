package cn.nukkit.block;

public class BlockCandleCakeBrown extends BlockCandleCake {

    public BlockCandleCakeBrown() {
        this(0);
    }

    public BlockCandleCakeBrown(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Brown Candle Cake";
    }

    @Override
    public int getId() {
        return BROWN_CANDLE_CAKE;
    }
}
