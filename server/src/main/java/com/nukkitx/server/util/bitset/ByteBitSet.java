package com.nukkitx.server.util.bitset;

public class ByteBitSet implements BitSet {
    private byte bitset;

    public ByteBitSet() {
        bitset = 0;
    }

    public ByteBitSet(byte bitset) {
        this.bitset = bitset;
    }

    public ByteBitSet(int bitset) {
        this.bitset = (byte) bitset;
    }

    public ByteBitSet(ByteBitSet bitSet) {
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
        return new short[]{bitset};
    }

    @Override
    public byte[] getBytes() {
        return new byte[]{bitset};
    }

    @Override
    public void clear() {
        bitset = 0;
    }

    public byte get() {
        return bitset;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ByteBitSet)) return false;
        ByteBitSet that = (ByteBitSet) o;
        return this.bitset == that.get();
    }

    private static void checkIndex(int index) {
        if (!(index >= 0 && index < 8)) {
            throw new IndexOutOfBoundsException("Expected value 0-7");
        }
    }
}
