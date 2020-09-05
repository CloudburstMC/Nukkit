package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.math.BigInteger;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class NukkitMath {

    public static int floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed math problem")
    public static int ceilDouble(double n) {
        int i = (int) n;
        return n > i ? i + 1 : i;
    }

    public static int floorFloat(float n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed math problem")
    public static int ceilFloat(float n) {
        int i = (int) n;
        return n > i ? i + 1 : i;
    }

    public static int randomRange(NukkitRandom random) {
        return randomRange(random, 0);
    }

    public static int randomRange(NukkitRandom random, int start) {
        return randomRange(random, 0, 0x7fffffff);
    }

    public static int randomRange(NukkitRandom random, int start, int end) {
        return start + (random.nextInt() % (end + 1 - start));
    }

    public static double round(double d) {
        return round(d, 0);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Optimized")
    public static double round(double d, int precision) {
        double pow = Math.pow(10, precision);
        return ((double) Math.round(d * pow)) / pow;
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    @Since("1.4.0.0-PN")
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static double getDirection(double diffX, double diffZ) {
        diffX = Math.abs(diffX);
        diffZ = Math.abs(diffZ);

        return Math.max(diffX, diffZ);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int bitLength(int data) {
        if (data < 0) {
            return 32;
        }

        if (data == 0) {
            return 1;
        }

        int bits = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int bitLength(long data) {
        if (data < 0) {
            return 64;
        }

        if (data == 0) {
            return 1;
        }

        int bits = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int bitLength(BigInteger data) {
        if (data.compareTo(BigInteger.ZERO) < 0) {
            throw new UnsupportedOperationException("Negative BigIntegers are not supported (nearly infinite bits)");
        }

        return data.bitLength();
    }

}
