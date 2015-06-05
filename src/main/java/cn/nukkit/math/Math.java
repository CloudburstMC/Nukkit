package cn.nukkit.math;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Math {

    public static double floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static double ceilDouble(double n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static float floorFloat(float n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static float ceilFloat(float n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

}
