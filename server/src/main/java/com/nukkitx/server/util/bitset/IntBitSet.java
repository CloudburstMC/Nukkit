package com.nukkitx.server.util.bitset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IntBitSet implements BitSet {
    private int bitset;

    public IntBitSet() {
        bitset = 0;
    }

    public IntBitSet(int bitset) {
        this.bitset = (byte) bitset;
    }

    public IntBitSet(IntBitSet bitSet) {
        this.bitset = bitSet.bitset;
    }

    @Override
    public void flip(int index) {
        bitset = BitUtil.flipBit(bitset, index);
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
    public long[] getLongs() {
        return new long[]{bitset};
    }

    @Override
    public int[] getInts() {
        return new int[]{bitset};
    }

    @Override
    public short[] getShorts() {
        ByteBuffer buffer = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(bitset);
        return buffer.asShortBuffer().array();
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(bitset);
        return buffer.array();
    }

    @Override
    public void clear() {
        bitset = 0;
    }

    public int get() {
        return bitset;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof IntBitSet)) return false;
        IntBitSet that = (IntBitSet) o;
        return this.bitset == that.get();
    }

    private static void checkIndex(int index) {
        if (!(index >= 0 && index < 32)) {
            throw new IndexOutOfBoundsException("Expected value 0-32");
        }
    }
}
