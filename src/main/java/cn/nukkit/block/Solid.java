package cn.nukkit.block;

import cn.nukkit.utils.RGBColor;

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
    public RGBColor getMapColor() {
        return RGBColor.stoneColor;
    }
}
