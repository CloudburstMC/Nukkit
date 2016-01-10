package cn.nukkit.block;

import cn.nukkit.math.Vector3;
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
    public Color getMapColor() {
        if (this.getFloorY() == 0) return Color.voidColor;
        Block down = getSide(Vector3.SIDE_DOWN);
        return down.getMapColor();
    }

}
