package cn.nukkit.level.format.anvil.palette;

import cn.nukkit.utils.ThreadCache;

/**
 * @author https://github.com/boy0001/
 */
public final class BitArray256 {

    protected final long[] data;

    private final int bitsPerEntry;

    public BitArray256(final int bitsPerEntry) {
        this.bitsPerEntry = bitsPerEntry;
        final int longLen = this.bitsPerEntry * 256 >> 6;
        this.data = new long[longLen];
    }

    public BitArray256(final BitArray256 other) {
        this.bitsPerEntry = other.bitsPerEntry;
        this.data = other.data.clone();
    }

    public final void setAt(final int index, final int value) {
        final int bitIndexStart = index * this.bitsPerEntry;
        final int longIndexStart = bitIndexStart >> 6;
        final int localBitIndexStart = bitIndexStart & 63;
        this.data[longIndexStart] = this.data[longIndexStart] & ~((long) ((1 << this.bitsPerEntry) - 1) << localBitIndexStart) | (long) value << localBitIndexStart;

        if (localBitIndexStart > 64 - this.bitsPerEntry) {
            final int longIndexEnd = longIndexStart + 1;
            final int localShiftStart = 64 - localBitIndexStart;
            final int localShiftEnd = this.bitsPerEntry - localShiftStart;
            this.data[longIndexEnd] = this.data[longIndexEnd] >>> localShiftEnd << localShiftEnd | (long) value >> localShiftStart;
        }
    }

    public final int getAt(final int index) {
        final int bitIndexStart = index * this.bitsPerEntry;

        final int longIndexStart = bitIndexStart >> 6;

        final int localBitIndexStart = bitIndexStart & 63;
        if (localBitIndexStart <= 64 - this.bitsPerEntry) {
            return (int) (this.data[longIndexStart] >>> localBitIndexStart & (1 << this.bitsPerEntry) - 1);
        } else {
            final int localShift = 64 - localBitIndexStart;
            return (int) ((this.data[longIndexStart] >>> localBitIndexStart | this.data[longIndexStart + 1] << localShift) & (1 << this.bitsPerEntry) - 1);
        }
    }

    public final void fromRaw(final int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            this.setAt(i, arr[i]);
        }
    }

    public BitArray256 grow(final int newBitsPerEntry) {
        final int amtGrow = newBitsPerEntry - this.bitsPerEntry;
        if (amtGrow <= 0) {
            return this;
        }
        final BitArray256 newBitArray = new BitArray256(newBitsPerEntry);

        final int[] buffer = ThreadCache.intCache256.get();
        this.toRaw(buffer);
        newBitArray.fromRaw(buffer);

        return newBitArray;
    }

    public BitArray256 growSlow(final int bitsPerEntry) {
        final BitArray256 newBitArray = new BitArray256(bitsPerEntry);
        for (int i = 0; i < 256; i++) {
            newBitArray.setAt(i, this.getAt(i));
        }
        return newBitArray;
    }

    public final int[] toRaw(final int[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = this.getAt(i);
        }
        return buffer;
    }

    public final int[] toRaw() {
        return this.toRaw(new int[256]);
    }

    @Override
    public BitArray256 clone() {
        return new BitArray256(this);
    }

}
