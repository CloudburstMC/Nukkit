package cn.nukkit.server.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedLime extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedLime() {
        this(0);
    }

    public BlockTerracottaGlazedLime(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIME_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Lime Glazed Terracotta";
    }
}
