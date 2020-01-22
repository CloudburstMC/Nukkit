package cn.nukkit.math;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public abstract class VectorMath {

    public static Vector2f getDirection2D(double azimuth) {
        return new Vector2f(Math.cos(azimuth), Math.sin(azimuth));
    }

}
