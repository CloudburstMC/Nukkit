package cn.nukkit.server.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedCyan extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedCyan() {
        this(0);
    }

    public BlockTerracottaGlazedCyan(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CYAN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Cyan Glazed Terracotta";
    }
}
