package cn.nukkit.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedGreen extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedGreen() {
        this(0);
    }

    public BlockTerracottaGlazedGreen(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GREEN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Green Glazed Terracotta";
    }
}
