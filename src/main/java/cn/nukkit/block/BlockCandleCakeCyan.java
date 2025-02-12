package cn.nukkit.block;

public class BlockCandleCakeCyan extends BlockCandleCake {

    public BlockCandleCakeCyan() {
        this(0);
    }

    public BlockCandleCakeCyan(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cyan Candle Cake";
    }

    @Override
    public int getId() {
        return CYAN_CANDLE_CAKE;
    }
}
