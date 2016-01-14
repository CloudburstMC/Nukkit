package cn.nukkit.block;

import cn.nukkit.utils.Color;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Solid extends Block {

    protected Solid(int meta) {
        super(meta);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public Color getColor() {
        return Color.stoneColor;
    }
}
