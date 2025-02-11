package cn.nukkit.block;

public class BlockCandleCakeGreen extends BlockCandleCake {

    public BlockCandleCakeGreen() {
        this(0);
    }

    public BlockCandleCakeGreen(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Green Candle Cake";
    }

    @Override
    public int getId() {
        return GREEN_CANDLE_CAKE;
    }
}
