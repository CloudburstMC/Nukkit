package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedPurple extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedPurple() {
        this(0);
    }

    public BlockTerracottaGlazedPurple(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PURPLE_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Purple Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
