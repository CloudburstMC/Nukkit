package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedWhite extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedWhite() {
        this(0);
    }

    public BlockTerracottaGlazedWhite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WHITE_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "White Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
