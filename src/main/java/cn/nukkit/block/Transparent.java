package cn.nukkit.block;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.RGBColor;

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
    public RGBColor getMapColor() {
        if (this.getFloorY() == 0) return RGBColor.voidColor;
        Block down = getSide(Vector3.SIDE_DOWN);
        return down.getMapColor();
    }

}
