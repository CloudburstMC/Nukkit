package cn.nukkit.level.util;

import cn.nukkit.math.MathHelper;
import com.google.common.base.Preconditions;
import java.util.Arrays;

public class Pow2BitArray implements BitArray {

    /**
     * Array used to store data
     */
    private final int[] words;

    /**
     * Palette version information
     */
    private final BitArrayVersion version;

    /**
     * Number of entries in this palette (<b>not</b> the length of the words array that internally backs this palette)
     */
    private final int size;

    Pow2BitArray(final BitArrayVersion version, final int size, final int[] words) {
        this.size = size;
        this.version = version;
        this.words = words;
        final int expectedWordsLength = MathHelper.ceil((float) size / version.entriesPerWord);
        if (words.length != expectedWordsLength) {
            throw new IllegalArgumentException("Invalid length given for storage, got: " + words.length +
                " but expected: " + expectedWordsLength);
        }
    }

    /**
     * Sets the entry at the given location to the given value
     */
    @Override
    public void set(final int index, final int value) {
        Preconditions.checkElementIndex(index, this.size);
        Preconditions.checkArgument(value >= 0 && value <= this.version.maxEntryValue,
            "Max value: %s. Received value", this.version.maxEntryValue, value);
        final int bitIndex = index * this.version.bits;
        final int arrayIndex = bitIndex >> 5;
        final int offset = bitIndex & 31;
        this.words[arrayIndex] = this.words[arrayIndex] & ~(this.version.maxEntryValue << offset) | (value & this.version.maxEntryValue) << offset;
    }

    /**
     * Gets the entry at the given index
     */
    @Override
    public int get(final int index) {
        Preconditions.checkElementIndex(index, this.size);
        final int bitIndex = index * this.version.bits;
        final int arrayIndex = bitIndex >> 5;
        final int wordOffset = bitIndex & 31;
        return this.words[arrayIndex] >>> wordOffset & this.version.maxEntryValue;
    }

    /**
     * Gets the long array that is used to store the data in this BitArray. This is useful for sending packet data.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int[] getWords() {
        return this.words;
    }

    @Override
    public BitArrayVersion getVersion() {
        return this.version;
    }

    @Override
    public BitArray copy() {
        return new Pow2BitArray(this.version, this.size, Arrays.copyOf(this.words, this.words.length));
    }

}
