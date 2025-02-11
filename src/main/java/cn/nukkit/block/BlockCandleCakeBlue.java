package cn.nukkit.block;

public class BlockCandleCakeBlue extends BlockCandleCake {

    public BlockCandleCakeBlue() {
        this(0);
    }

    public BlockCandleCakeBlue(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blue Candle Cake";
    }

    @Override
    public int getId() {
        return BLUE_CANDLE_CAKE;
    }
}
