package cn.nukkit.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedBlack extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedBlack() {
        this(0);
    }

    public BlockTerracottaGlazedBlack(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLACK_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Black Glazed Terracotta";
    }
}
