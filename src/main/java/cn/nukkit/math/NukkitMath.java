package cn.nukkit.math;

import com.nukkitx.math.vector.Vector3f;

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

    public static double getDirection(double diffX, double diffZ) {
        diffX = Math.abs(diffX);
        diffZ = Math.abs(diffZ);

        return diffX > diffZ ? diffX : diffZ;
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v2 vector
     * @param x  x value
     * @return intermediate vector
     */
    public static Vector3f getIntermediateWithXValue(Vector3f v, Vector3f v2, float x) {
        float xDiff = v2.getX() - v.getX();
        float yDiff = v2.getY() - v.getY();
        float zDiff = v2.getZ() - v.getZ();
        if (xDiff * xDiff < 0.0000001) {
            return null;
        }
        float f = (x - v.getX()) / xDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return Vector3f.from(v.getX() + xDiff * f, v.getY() + yDiff * f, v.getZ() + zDiff * f);
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v2 vector
     * @param y  y value
     * @return intermediate vector
     */
    public static Vector3f getIntermediateWithYValue(Vector3f v, Vector3f v2, float y) {
        float xDiff = v2.getX() - v.getX();
        float yDiff = v2.getY() - v.getY();
        float zDiff = v2.getZ() - v.getZ();
        if (yDiff * yDiff < 0.0000001) {
            return null;
        }
        float f = (y - v.getY()) / yDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return Vector3f.from(v.getX() + xDiff * f, v.getY() + yDiff * f, v.getZ() + zDiff * f);
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v2 vector
     * @param z  z value
     * @return intermediate vector
     */
    public static Vector3f getIntermediateWithZValue(Vector3f v, Vector3f v2, float z) {
        float xDiff = v2.getX() - v.getX();
        float yDiff = v2.getY() - v.getY();
        float zDiff = v2.getZ() - v.getZ();
        if (zDiff * zDiff < 0.0000001) {
            return null;
        }
        float f = (z - v.getZ()) / zDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return Vector3f.from(v.getX() + xDiff * f, v.getY() + yDiff * f, v.getZ() + zDiff * f);
        }
    }
}
