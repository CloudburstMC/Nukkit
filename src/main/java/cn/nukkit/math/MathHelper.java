package cn.nukkit.math;

import java.util.Random;

public class MathHelper {
    private static float[] a = new float[65536];

    static {
        for (int i = 0; i < 65536; i++)
            a[i] = (float) Math.sin(i * 3.141592653589793D * 2.0D / 65536.0D);
    }

    private static final int BIG_ENOUGH_INT = 30_000_000;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;

    private static final int[] MULTIPLY_DE_BRUJIN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

    private MathHelper() {
    }

    public static float sqrt(float paramFloat) {
        return (float) Math.sqrt(paramFloat);
    }

    public static float sin(float paramFloat) {
        return a[((int) (paramFloat * 10430.378F) & 0xFFFF)];
    }

    public static float cos(float paramFloat) {
        return a[((int) (paramFloat * 10430.378F + 16384.0F) & 0xFFFF)];
    }

    public static float sin(double paramFloat) {
        return a[((int) (paramFloat * 10430.378F) & 0xFFFF)];
    }

    public static float cos(double paramFloat) {
        return a[((int) (paramFloat * 10430.378F + 16384.0F) & 0xFFFF)];
    }

    public static int floor(double d0) {
        int i = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }

    public static long floor_double_long(double d) {
        long l = (long) d;
        return d >= (double) l ? l : l - 1L;
    }

    public static int floor_float_int(float f) {
        int i = (int) f;
        return f >= i ? i : i - 1;
    }

    public static int abs(int number) {
        if (number > 0) {
            return number;
        } else {
            return -number;
        }
    }

    public static int log2(int bits) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(bits);
    }

    /**
     * Returns a random number between min and max, inclusive.
     *
     * @param random The random number generator.
     * @param min    The minimum value.
     * @param max    The maximum value.
     * @return A random number between min and max, inclusive.
     */
    public static int getRandomNumberInRange(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public static double max(double first, double second, double third, double fourth) {
        if (first > second && first > third && first > fourth) {
            return first;
        }
        if (second > third && second > fourth) {
            return second;
        }
        if (third > fourth) {
            return third;
        }
        return fourth;
    }

    public static int ceil(float floatNumber) {
        int truncated = (int) floatNumber;
        return floatNumber > truncated ? truncated + 1 : truncated;
    }

    public static int clamp(int check, int min, int max) {
        return check > max ? max : (check < min ? min : check);
    }

    public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide) {
        return slide < 0.0D ? lowerBnd : (slide > 1.0D ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
    }

    public static float denormalizeClamp(float lowerBnd, float upperBnd, float slide) {
        return slide < 0.0f ? lowerBnd : (slide > 1.0f ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
    }

    public static int fastCeil(float val) {
        return BIG_ENOUGH_INT - (int) (BIG_ENOUGH_FLOOR - val);
    }

    public static int fastFloor(float val) {
        return (int) (val + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static int roundUp(final int int_1, int int_2) {
        if (int_2 == 0) {
            return 0;
        }
        if (int_1 == 0) {
            return int_2;
        }
        if (int_1 < 0) {
            int_2 *= -1;
        }
        final int int_3 = int_1 % int_2;
        if (int_3 == 0) {
            return int_1;
        }
        return int_1 + int_2 - int_3;
    }

    public static int smallestEncompassingPowerOfTwo(final int int_1) {
        int int_2 = int_1 - 1;
        int_2 |= int_2 >> 1;
        int_2 |= int_2 >> 2;
        int_2 |= int_2 >> 4;
        int_2 |= int_2 >> 8;
        int_2 |= int_2 >> 16;
        return int_2 + 1;
    }

    private static boolean isPowerOfTwo(final int int_1) {
        return int_1 != 0 && (int_1 & int_1 - 1) == 0x0;
    }

    public static int log2DeBrujin(int int_1) {
        int_1 = (isPowerOfTwo(int_1) ? int_1 : smallestEncompassingPowerOfTwo(int_1));
        return MathHelper.MULTIPLY_DE_BRUJIN_BIT_POSITION[(int) (int_1 * 125613361L >> 27) & 0x1F];
    }
}
