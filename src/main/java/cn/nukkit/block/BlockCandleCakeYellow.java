package cn.nukkit.block;

public class BlockCandleCakeYellow extends BlockCandleCake {

    public BlockCandleCakeYellow() {
        this(0);
    }

    public BlockCandleCakeYellow(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Yellow Candle Cake";
    }

    @Override
    public int getId() {
        return YELLOW_CANDLE_CAKE;
    }
}
