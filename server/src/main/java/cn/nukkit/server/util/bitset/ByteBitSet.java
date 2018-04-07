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
