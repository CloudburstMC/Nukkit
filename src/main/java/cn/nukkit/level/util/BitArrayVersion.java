package cn.nukkit.level.util;

public enum BitArrayVersion {
    V16(16, 2, null),
    V8(8, 4, BitArrayVersion.V16),
    V6(6, 5, BitArrayVersion.V8), // 2 bit padding
    V5(5, 6, BitArrayVersion.V6), // 2 bit padding
    V4(4, 8, BitArrayVersion.V5),
    V3(3, 10, BitArrayVersion.V4), // 2 bit padding
    V2(2, 16, BitArrayVersion.V3),
    V1(1, 32, BitArrayVersion.V2);

    final byte bits;

    final byte entriesPerWord;

    final int maxEntryValue;

    final BitArrayVersion next;

    BitArrayVersion(final int bits, final int entriesPerWord, final BitArrayVersion next) {
        this.bits = (byte) bits;
        this.entriesPerWord = (byte) entriesPerWord;
        this.maxEntryValue = (1 << this.bits) - 1;
        this.next = next;
    }

    public static BitArrayVersion get(final int version, final boolean read) {
        for (final BitArrayVersion ver : BitArrayVersion.values()) {
            if (!read && ver.entriesPerWord <= version || read && ver.bits == version) {
                return ver;
            }
        }
        throw new IllegalArgumentException("Invalid palette version: " + version);
    }

    public BitArray createPalette(final int size) {
        return this.createPalette(size, new int[this.getWordsForSize(size)]);
    }

    public byte getId() {
        return this.bits;
    }

    public int getWordsForSize(final int size) {
        return size / this.entriesPerWord + (size % this.entriesPerWord == 0 ? 0 : 1);
    }

    public int getMaxEntryValue() {
        return this.maxEntryValue;
    }

    public BitArrayVersion next() {
        return this.next;
    }

    public BitArray createPalette(final int size, final int[] words) {
        if (this == BitArrayVersion.V3 || this == BitArrayVersion.V5 || this == BitArrayVersion.V6) {
            // Padded palettes aren't able to use bitwise operations due to their padding.
            return new PaddedBitArray(this, size, words);
        } else {
            return new Pow2BitArray(this, size, words);
        }
    }
}
