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

public class ByteBitSet implements BitSet {
    private byte bitset;

    public ByteBitSet() {
        bitset = 0;
    }

    public ByteBitSet(int bitset) {
        this.bitset = (byte) bitset;
    }

    public ByteBitSet(ByteBitSet bitSet) {
        this.bitset = bitSet.bitset;
    }

    @Override
    public void set(int index, boolean value) {
        bitset = BitUtil.setBit(bitset, index, value);
    }

    @Override
    public boolean get(int index) {
        return BitUtil.getBit(bitset, index);
    }

    @Override
    public long getAsLong() {
        return 0;
    }

    @Override
    public int getAsInt() {
        return bitset;
    }

    @Override
    public short getAsShort() {
        return bitset;
    }

    @Override
    public byte getAsByte() {
        return bitset;
    }

    public byte get() {
        return bitset;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof BitSet)) return false;
        BitSet that = (BitSet) o;
        return this.bitset == that.getAsByte();
    }
}
