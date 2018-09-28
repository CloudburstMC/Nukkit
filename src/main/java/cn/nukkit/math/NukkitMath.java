package cn.nukkit.math;

import cn.nukkit.utils.MainLogger;
import cn.nukkit.Server;

import java.lang.Math;

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

    public static double round(double d, int precision) {
        return ((double) Math.round(d * Math.pow(10, precision))) / Math.pow(10, precision);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }
    
    public static double[] calculateQuadratic(double a, double b, double c) {
        double square = (b * b) - (4 * a * c);
        if (square > 0) {
            double x1 = (-b + Math.sqrt(square)) / 2;
            double x2 = (-b - Math.sqrt(square)) / 2;
            double[] list = {
                x1,
                x2
            };
            return list;
        } else if (square == 0) {
            double x = (-b / (2 * a));
            double[] one = {
                x
            };
            return one;
        } else {
            String real = String.valueOf(-b / 2);
            String x1Imaginary = String.valueOf(" + " + Math.sqrt(-square) / 2) + "i";
            String x2Imaginary = String.valueOf(" - " + Math.sqrt(-square) / 2) + "i";
            MainLogger logger = Server.getInstance().getLogger();
            logger.info("X1: " + real + x1Imaginary);
            logger.info("X2: " + real + x2Imaginary);
            double[] nulls = {
                0
            };
            return nulls;
        }
    }

    public static double getDirection(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return d0 > d1 ? d0 : d1;
    }

}
