package cn.nukkit.block;

public class BlockCandleCakePurple extends BlockCandleCake {

    public BlockCandleCakePurple() {
        this(0);
    }

    public BlockCandleCakePurple(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Purple Candle Cake";
    }

    @Override
    public int getId() {
        return PURPLE_CANDLE_CAKE;
    }
}
