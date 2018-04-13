package cn.nukkit.server.util.bitset;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SyncLongBitSet implements BitSet {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private long bitset;

    public SyncLongBitSet() {
        bitset = 0;
    }

    public SyncLongBitSet(long bitset) {
        this.bitset = bitset;
    }

    public SyncLongBitSet(BitSet bitSet) {
        this.bitset = bitSet.getAsLong();
    }

    @Override
    public void set(int index, boolean value) {
        checkIndex(index);
        writeLock.lock();
        try {
            bitset = BitUtil.setBit(bitset, index, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean get(int index) {
        checkIndex(index);
        readLock.lock();
        try {
            return BitUtil.getBit(bitset, index);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public long getAsLong() {
        return get();
    }

    @Override
    public int getAsInt() {
        return (int) get();
    }

    @Override
    public short getAsShort() {
        return (short) get();
    }

    @Override
    public byte getAsByte() {
        return (byte) get();
    }

    public long get() {
        readLock.lock();
        try {
            return bitset;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof BitSet)) return false;
        BitSet that = (BitSet) o;
        return this.bitset == that.getAsLong();
    }

    private static void checkIndex(int index) {
        if (!(index >= 0 && index < 64)) {
            throw new IndexOutOfBoundsException("Expected value 0-63");
        }
    }
}
