package cn.nukkit.block;

public class BlockCandleCyan extends BlockCandle {

    public BlockCandleCyan() {
        this(0);
    }

    public BlockCandleCyan(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cyan Candle";
    }

    @Override
    public int getId() {
        return CYAN_CANDLE;
    }
}
