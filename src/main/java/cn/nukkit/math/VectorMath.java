package cn.nukkit.math;

import com.nukkitx.math.vector.Vector2f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public abstract class VectorMath {

    public static Vector2f getDirection2D(double azimuth) {
        return Vector2f.from(Math.cos(azimuth), Math.sin(azimuth));
    }

}
