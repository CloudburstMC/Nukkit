package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedPink extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedPink() {
        this(0);
    }

    public BlockTerracottaGlazedPink(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PINK_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Pink Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
