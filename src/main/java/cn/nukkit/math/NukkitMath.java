package cn.nukkit.math;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitMath {

    public static int floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int ceilDouble(double n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int floorFloat(float n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int ceilFloat(float n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int randomRange(Random random) {
        return randomRange(random, 0);
    }

    public static int randomRange(Random random, int start) {
        return randomRange(random, 0, 0x7fffffff);
    }

    public static int randomRange(Random random, int start, int end) {
        return start + (random.nextInt() % (end + 1 - start));
    }

}
