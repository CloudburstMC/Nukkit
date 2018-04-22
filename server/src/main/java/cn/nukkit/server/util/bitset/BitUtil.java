package cn.nukkit.server.util.bitset;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BitUtil {

    public static boolean getBit(long bitset, int index) {
        return ((bitset >>> index) & 1) != 0;
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
        return value ? bitset | (1 << index) : bitset & ~(1 << index);
    }

    public static byte flipBit(byte bitset, int index) {
        return (byte) flipBit((long) bitset, index);
    }

    public static short flipBit(short bitset, int index) {
        return (short) flipBit((long) bitset, index);
    }

    public static int flipBit(int bitset, int index) {
        return (int) flipBit((long) bitset, index);
    }

    public static long flipBit(long bitset, int index) {
        return bitset ^ (1 << index);
    }
}
