package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Sponge extends Solid {


    public Sponge() {
        this(0);
    }

    public Sponge(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPONGE;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public String getName() {
        return "Sponge";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CLOTH_BLOCK_COLOR;
    }
}
