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
        return bitset ^= 1 << index;
    }
}
