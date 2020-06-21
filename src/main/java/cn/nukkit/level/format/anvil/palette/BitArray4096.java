package cn.nukkit.level.format.anvil.palette;

import cn.nukkit.utils.ThreadCache;

/**
 * @author https://github.com/boy0001/
 */
public final class BitArray4096 {

    private final int bitsPerEntry;

    private final int maxSeqLocIndex;

    private final int maxEntryValue;

    private final long[] data;

    public BitArray4096(final int bitsPerEntry) {
        this.bitsPerEntry = bitsPerEntry;
        this.maxSeqLocIndex = 64 - bitsPerEntry;
        this.maxEntryValue = (1 << bitsPerEntry) - 1;
        final int longLen = this.bitsPerEntry * 4096 >> 6;
        this.data = new long[longLen];
    }

    public final void setAt(final int index, final int value) {
        if (this.data.length == 0) {
            return;
        }
        final int bitIndexStart = index * this.bitsPerEntry;
        final int longIndexStart = bitIndexStart >> 6;
        final int localBitIndexStart = bitIndexStart & 63;
        this.data[longIndexStart] = this.data[longIndexStart] & ~((long) this.maxEntryValue << localBitIndexStart) | (long) value << localBitIndexStart;

        if (localBitIndexStart > this.maxSeqLocIndex) {
            final int longIndexEnd = longIndexStart + 1;
            final int localShiftStart = 64 - localBitIndexStart;
            final int localShiftEnd = this.bitsPerEntry - localShiftStart;
            this.data[longIndexEnd] = this.data[longIndexEnd] >>> localShiftEnd << localShiftEnd | (long) value >> localShiftStart;
        }
    }

    public final int getAt(final int index) {
        if (this.data.length == 0) {
            return 0;
        }
        final int bitIndexStart = index * this.bitsPerEntry;

        final int longIndexStart = bitIndexStart >> 6;

        final int localBitIndexStart = bitIndexStart & 63;
        if (localBitIndexStart <= this.maxSeqLocIndex) {
            return (int) (this.data[longIndexStart] >>> localBitIndexStart & this.maxEntryValue);
        } else {
            final int localShift = 64 - localBitIndexStart;
            return (int) ((this.data[longIndexStart] >>> localBitIndexStart | this.data[longIndexStart + 1] << localShift) & this.maxEntryValue);
        }
    }

    public final void fromRawSlow(final char[] arr) {
        for (int i = 0; i < arr.length; i++) {
            this.setAt(i, arr[i]);
        }
    }

    public final void fromRaw(final char[] arr) {
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
                l |= (long) lastVal << localStart;
            }
            if (localStart < 64) {
                if (i != dataLength - 1) {
                    lastVal = arr[arrI++];
                    final int shift = 64 - localStart;

                    nextVal = lastVal >> shift;

                    l |= lastVal - (nextVal << shift) << localStart;

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

    public BitArray4096 grow(final int newBitsPerEntry) {
        final int amtGrow = newBitsPerEntry - this.bitsPerEntry;
        if (amtGrow <= 0) {
            return this;
        }
        final BitArray4096 newBitArray = new BitArray4096(newBitsPerEntry);

        final char[] buffer = ThreadCache.charCache4096.get();
        this.toRaw(buffer);
        newBitArray.fromRaw(buffer);

        return newBitArray;
    }

    public BitArray4096 growSlow(final int bitsPerEntry) {
        final BitArray4096 newBitArray = new BitArray4096(bitsPerEntry);
        for (int i = 0; i < 4096; i++) {
            newBitArray.setAt(i, this.getAt(i));
        }
        return newBitArray;
    }

    public final char[] toRawSlow() {
        final char[] arr = new char[4096];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (char) this.getAt(i);
        }
        return arr;
    }

    public final char[] toRaw() {
        return this.toRaw(new char[4096]);
    }

    protected final char[] toRaw(final char[] buffer) {
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
                    final int localShift = bitsPerEntry - localStart;
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
