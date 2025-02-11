package cn.nukkit.block;

public class BlockCandleCakeWhite extends BlockCandleCake {

    public BlockCandleCakeWhite() {
        this(0);
    }

    public BlockCandleCakeWhite(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "White Candle Cake";
    }

    @Override
    public int getId() {
        return WHITE_CANDLE_CAKE;
    }
}
