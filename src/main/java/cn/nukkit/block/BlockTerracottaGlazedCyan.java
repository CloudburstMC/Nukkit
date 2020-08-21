package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedCyan extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedCyan() {
        this(0);
    }

    public BlockTerracottaGlazedCyan(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CYAN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Cyan Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
