package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedRed extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedRed() {
        this(0);
    }

    public BlockTerracottaGlazedRed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Red Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
