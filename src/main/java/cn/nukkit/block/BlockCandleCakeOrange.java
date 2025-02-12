package cn.nukkit.block;

public class BlockCandleCakeOrange extends BlockCandleCake {

    public BlockCandleCakeOrange() {
        this(0);
    }

    public BlockCandleCakeOrange(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Orange Candle Cake";
    }

    @Override
    public int getId() {
        return ORANGE_CANDLE_CAKE;
    }
}
