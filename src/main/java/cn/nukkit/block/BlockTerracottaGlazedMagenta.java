package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedMagenta extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedMagenta() {
        this(0);
    }

    public BlockTerracottaGlazedMagenta(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MAGENTA_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Magenta Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
