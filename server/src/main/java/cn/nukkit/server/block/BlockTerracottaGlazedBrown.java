package cn.nukkit.server.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedBrown extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedBrown() {
        this(0);
    }

    public BlockTerracottaGlazedBrown(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BROWN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Brown Glazed Terracotta";
    }
}
