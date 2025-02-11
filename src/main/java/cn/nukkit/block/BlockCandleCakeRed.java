package cn.nukkit.block;

public class BlockCandleCakeRed extends BlockCandleCake {

    public BlockCandleCakeRed() {
        this(0);
    }

    public BlockCandleCakeRed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Red Candle Cake";
    }

    @Override
    public int getId() {
        return RED_CANDLE_CAKE;
    }
}
