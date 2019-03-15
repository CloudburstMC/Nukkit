package cn.nukkit.level.format.anvil.palette;

import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.BinaryStream;

public class Palette {

    private BinaryStream data = new BinaryStream();
    private Version version;

    // Output indexes
    private short[] output;

    // Input bitset
    private int bits = 0;
    private int inputIndex = 0;
    private int wordsWritten = 0;

    /**
     * Construct a new reader for the given palette version
     *
     * @param version of the palette or the amount of blocks we want to store in one word
     * @param read do we read or write to this palette?
     */
    public Palette(int version, boolean read) {
        for (Version paletteVersionCanidate : Version.values()) {
            if ((!read && paletteVersionCanidate.getWordsAmount() <= version && paletteVersionCanidate.getPaddingAmount() == 0) || (read && paletteVersionCanidate.getId() == version)) {
                this.version = paletteVersionCanidate;
                break;
            }
        }
        if (this.version == null) {
            throw new IllegalArgumentException("Palette version " + version + " is unknown");
        }
    }

    public void addIndexIDs(int[] indexIDs) {
        for (int id : indexIDs) {
            // Check if old input is full and we need a new one
            if (this.wordsWritten == this.version.getWordsAmount()) {
                // Write to output
                this.data.putLInt(this.bits);

                // New input
                this.bits = 0;
                this.inputIndex = 0;
                this.wordsWritten = 0;
            }

            // Write id
            while (id != 0) {
                if (id % 2 != 0) {
                    this.bits |= (1 << this.inputIndex);
                }

                ++this.inputIndex;
                id >>>= 1;
            }

            // Increment written words
            this.wordsWritten++;

            // Set the index correct
            this.inputIndex = this.wordsWritten * this.version.getId();
        }
    }

    public byte[] finish() {
        this.data.putLInt(this.bits);
        return this.data.getBuffer();
    }

    public short[] getIndexes() {
        // Do we need to read first?
        if (this.output == null) {
            this.output = new short[4096];

            // We need the amount of iterations
            int iterations = MathHelper.fastCeil(4096 / (float) this.version.getWordsAmount());
            for (int i = 0; i < iterations; i++) {
                int currentData = this.data.getLInt();
                int index = 0;

                for (byte b = 0; b < this.version.getWordsAmount(); b++) {
                    short val = 0;
                    int innerShiftIndex = 0;

                    for (byte i1 = 0; i1 < this.version.getId(); i1++) {
                        if ((currentData & (1 << index++)) != 0) {
                            val ^= 1 << innerShiftIndex;
                        }
                        innerShiftIndex++;
                    }

                    int setIndex = (i * this.version.getWordsAmount()) + b;
                    if (setIndex < 4096) {
                        this.output[setIndex] = val;
                    }
                }
            }
        }
        return this.output;
    }

    public Version getVersion() {
        return this.version;
    }

    public enum Version {
        V1(1, 32),
        V2(2, 16),
        V3(3, 10, 2),
        V4(4, 8),
        V5(5, 6, 2),
        V6(6, 5, 2),
        V8(8, 4),
        V16(16, 2);

        private final byte id;
        private final byte wordsAmount;
        private final byte paddingAmount;

        Version(int id, int wordsAmount) {
            this(id, wordsAmount, 0);
        }

        Version(int id, int wordsAmount, int paddingAmount) {
            this.id = (byte) id;
            this.wordsAmount = (byte) wordsAmount;
            this.paddingAmount = (byte) paddingAmount;
        }

        public byte getId() {
            return this.id;
        }

        public byte getWordsAmount() {
            return this.wordsAmount;
        }

        public byte getPaddingAmount() {
            return this.paddingAmount;
        }
    }
}
