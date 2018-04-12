/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.math;

import java.util.Random;

public class MathHelper {
    private static float[] a = new float[65536];

    public static float sqrt(float paramFloat) {
        return (float) Math.sqrt(paramFloat);
    }

    public static float sin(float paramFloat) {
        return a[((int) (paramFloat * 10430.378F) & 0xFFFF)];
    }

    public static float cos(float paramFloat) {
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

    public static int abs(int number) {
        if (number > 0) {
            return number;
        } else {
            return -number;
        }
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

    static {
        for (int i = 0; i < 65536; i++)
            a[i] = (float) Math.sin(i * 3.141592653589793D * 2.0D / 65536.0D);
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

    private MathHelper() {
    }
}