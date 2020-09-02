package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
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

    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
