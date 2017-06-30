package cn.nukkit.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedLightBlue extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedLightBlue() {
        this(0);
    }

    public BlockTerracottaGlazedLightBlue(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIGHT_BLUE_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Light Blue Glazed Terracotta";
    }
}
