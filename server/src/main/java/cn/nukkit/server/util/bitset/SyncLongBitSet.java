package cn.nukkit.server.util.bitset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        this.bitset = bitSet.getLongs()[1];
    }

    @Override
    public void flip(int index) {
        checkIndex(index);
        writeLock.lock();
        try {
            bitset = BitUtil.flipBit(bitset, index);
        } finally {
            writeLock.unlock();
        }
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
    public long[] getLongs() {
        return new long[]{get()};
    }

    @Override
    public int[] getInts() {
        ByteBuffer buffer = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(get());
        return buffer.asIntBuffer().array();
    }

    @Override
    public short[] getShorts() {
        ByteBuffer buffer = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(get());
        return buffer.asShortBuffer().array();
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(get());
        return buffer.array();
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            bitset = 0;
        } finally {
            writeLock.unlock();
        }
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
        if (!(o instanceof SyncLongBitSet)) return false;
        SyncLongBitSet that = (SyncLongBitSet) o;
        return this.bitset == that.get();
    }

    private static void checkIndex(int index) {
        if (!(index >= 0 && index < 64)) {
            throw new IndexOutOfBoundsException("Expected value 0-63");
        }
    }
}
