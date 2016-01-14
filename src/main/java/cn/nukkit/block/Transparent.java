package cn.nukkit.block;

import cn.nukkit.utils.Color;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Transparent extends Block {

    protected Transparent(int meta) {
        super(meta);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public Color getColor() {
        return Color.transparentColor;
    }

}
