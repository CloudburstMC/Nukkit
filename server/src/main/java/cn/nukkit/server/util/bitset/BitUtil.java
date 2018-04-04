package cn.nukkit.server.util.bitset;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BitUtil {

    public static boolean getBit(long bitset, int index) {
        return (bitset & index) == index;
    }

    public static byte setBit(byte bitset, int index, boolean value) {
        return (byte) setBit((long) bitset, index, value);
    }

    public static short setBit(short bitset, int index, boolean value) {
        return (short) setBit((long) bitset, index, value);
    }

    public static int setBit(int bitset, int index, boolean value) {
        return (int) setBit((long) bitset, index, value);
    }

    public static long setBit(long bitset, int index, boolean value) {
        return value ? bitset | index : bitset & ~index;
    }
}
