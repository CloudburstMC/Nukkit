package cn.nukkit.level.format.anvil.palette;

import cn.nukkit.utils.ThreadCache;

/**
 * @author https://github.com/boy0001/
 */
public final class BitArray {

    private final int bitsPerEntry;
    private final int maxSeqLocIndex;
    private final int maxEntryValue;
    private final long[] data;

    public BitArray(int bitsPerEntry) {
        this.bitsPerEntry = bitsPerEntry;
        this.maxSeqLocIndex = 64 - bitsPerEntry;
        maxEntryValue = (1 << bitsPerEntry) - 1;
        int longLen = (this.bitsPerEntry * 4096) >> 6;
        this.data = new long[longLen];
    }

    public final void setAt(int index, int value) {
        int bitIndexStart = index * bitsPerEntry;
        int longIndexStart = bitIndexStart >> 6;
        int localBitIndexStart = bitIndexStart & 63;
        this.data[longIndexStart] = this.data[longIndexStart] & ~((long) maxEntryValue << localBitIndexStart) | ((long) value) << localBitIndexStart;

        if(localBitIndexStart > maxSeqLocIndex) {
            int longIndexEnd = longIndexStart + 1;
            int localShiftStart = 64 - localBitIndexStart;
            int localShiftEnd = bitsPerEntry - localShiftStart;
            this.data[longIndexEnd] = this.data[longIndexEnd] >>> localShiftEnd << localShiftEnd | (((long) value) >> localShiftStart);
        }
    }

    public final int getAt(int index) {
        int bitIndexStart = index * bitsPerEntry;

        int longIndexStart = bitIndexStart >> 6;

        int localBitIndexStart = bitIndexStart & 63;
        if(localBitIndexStart <= maxSeqLocIndex) {
            return (int)(this.data[longIndexStart] >>> localBitIndexStart & maxEntryValue);
        } else {
            int localShift = 64 - localBitIndexStart;
            return (int) ((this.data[longIndexStart] >>> localBitIndexStart | this.data[longIndexStart + 1] << localShift) & maxEntryValue);
        }
    }

    public final void fromRawSlow(char[] arr) {
        for (int i = 0; i < arr.length; i++) {
            setAt(i, arr[i]);
        }
    }

    public final void fromRaw(char[] arr) {
        final long[] data = this.data;
        final int dataLength = data.length;
        final int bitsPerEntry = this.bitsPerEntry;
        final int maxEntryValue = this.maxEntryValue;
        final int maxSeqLocIndex = this.maxSeqLocIndex;

        int localStart = 0;
        char lastVal;
        int arrI = 0;
        long l = 0;
        long nextVal;
        for (int i = 0; i < dataLength; i++) {
            for (; localStart <= maxSeqLocIndex; localStart += bitsPerEntry) {
                lastVal = arr[arrI++];
                l |= ((long) lastVal << localStart);
            }
            if (localStart < 64) {
                if (i != dataLength - 1) {
                    lastVal = arr[arrI++];
                    int shift = 64 - localStart;

                    nextVal = lastVal >> shift;

                    l |= ((lastVal - (nextVal << shift)) << localStart);

                    data[i] = l;
                    data[i + 1] = l = nextVal;

                    localStart -= maxSeqLocIndex;
                }
            } else {
                localStart = 0;
                data[i] = l;
                l = 0;
            }
        }
    }

    public BitArray grow(int newBitsPerEntry) {
        int amtGrow = newBitsPerEntry - this.bitsPerEntry;
        if (amtGrow <= 0) return this;
        BitArray newBitArray = new BitArray(newBitsPerEntry);

        char[] buffer = ThreadCache.charCache4096.get();
        toRaw(buffer);
        newBitArray.fromRaw(buffer);

        return newBitArray;
    }

    public BitArray growSlow(int bitsPerEntry) {
        BitArray newBitArray = new BitArray(bitsPerEntry);
        for (int i = 0; i < 4096; i++) {
            newBitArray.setAt(i, getAt(i));
        }
        return newBitArray;
    }

    public final char[] toRawSlow() {
        char[] arr = new char[4096];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (char) getAt(i);
        }
        return arr;
    }

    public final char[] toRaw() {
        return toRaw(new char[4096]);
    }

    protected final char[] toRaw(char[] buffer) {
        final long[] data = this.data;
        final int dataLength = data.length;
        final int bitsPerEntry = this.bitsPerEntry;
        final int maxEntryValue = this.maxEntryValue;
        final int maxSeqLocIndex = this.maxSeqLocIndex;

        int localStart = 0;
        char lastVal;
        int arrI = 0;
        long l;
        for (int i = 0; i < dataLength; i++) {
            l = data[i];
            for (; localStart <= maxSeqLocIndex; localStart += bitsPerEntry) {
                lastVal = (char) (l >>> localStart & maxEntryValue);
                buffer[arrI++] = lastVal;
            }
            if (localStart < 64) {
                if (i != dataLength - 1) {
                    lastVal = (char) (l >>> localStart);
                    localStart -= maxSeqLocIndex;
                    l = data[i + 1];
                    int localShift = bitsPerEntry - localStart;
                    lastVal |= l << localShift;
                    lastVal &= maxEntryValue;
                    buffer[arrI++] = lastVal;
                }
            } else {
                localStart = 0;
            }
        }
        return buffer;
    }
}
