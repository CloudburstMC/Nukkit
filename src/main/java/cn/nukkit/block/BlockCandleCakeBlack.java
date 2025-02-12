package cn.nukkit.block;

public class BlockCandleCakeBlack extends BlockCandleCake {

    public BlockCandleCakeBlack() {
        this(0);
    }

    public BlockCandleCakeBlack(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Black Candle Cake";
    }

    @Override
    public int getId() {
        return BLACK_CANDLE_CAKE;
    }
}
