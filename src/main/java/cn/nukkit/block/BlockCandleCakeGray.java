package cn.nukkit.block;

public class BlockCandleCakeGray extends BlockCandleCake {

    public BlockCandleCakeGray() {
        this(0);
    }

    public BlockCandleCakeGray(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Gray Candle Cake";
    }

    @Override
    public int getId() {
        return GRAY_CANDLE_CAKE;
    }
}
