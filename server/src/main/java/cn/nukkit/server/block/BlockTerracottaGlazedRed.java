package cn.nukkit.server.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedRed extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedRed() {
        this(0);
    }

    public BlockTerracottaGlazedRed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Red Glazed Terracotta";
    }
}
