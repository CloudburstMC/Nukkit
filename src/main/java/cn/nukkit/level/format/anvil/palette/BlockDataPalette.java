package cn.nukkit.level.format.anvil.palette;

import cn.nukkit.Server;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.ThreadCache;
import com.google.common.base.Preconditions;
import java.util.Arrays;

/**
 * @author https://github.com/boy0001/
 */
public final class BlockDataPalette implements Cloneable {

    private static final int BLOCK_SIZE = 4096;

    private volatile char[] rawData;

    private volatile BitArray4096 encodedData;

    private volatile CharPalette palette;

    // TODO compress unused sections
    // private byte[] compressedData;

    public BlockDataPalette() {
        this(new char[BlockDataPalette.BLOCK_SIZE]);
    }

    public BlockDataPalette(final char[] rawData) {
        Preconditions.checkArgument(rawData.length == BlockDataPalette.BLOCK_SIZE, "Data is not 4096");
        this.rawData = rawData;
    }

    public synchronized char[] getRaw() {
        final CharPalette palette = this.palette;
        final BitArray4096 encodedData = this.encodedData;
        this.encodedData = null;
        this.palette = null;

        char[] raw = this.rawData;
        if (raw == null && palette != null) {
            if (encodedData != null) {
                raw = encodedData.toRaw();
            } else {
                raw = new char[BlockDataPalette.BLOCK_SIZE];
            }
            for (int i = 0; i < BlockDataPalette.BLOCK_SIZE; i++) {
                raw[i] = palette.getKey(raw[i]);
            }
        } else {
            raw = new char[BlockDataPalette.BLOCK_SIZE];
        }
        this.rawData = raw;
        return this.rawData;
    }

    public int getBlockData(final int x, final int y, final int z) {
        return this.getFullBlock(x, y, z) & 0xF;
    }

    public int getBlockId(final int x, final int y, final int z) {
        return this.getFullBlock(x, y, z) >> 4;
    }

    public void setBlockId(final int x, final int y, final int z, final int id) {
        this.setFullBlock(x, y, z, (char) (id << 4));
    }

    public synchronized void setBlockData(final int x, final int y, final int z, final int data) {
        final int index = this.getIndex(x, y, z);
        final char[] raw = this.getCachedRaw();

        if (raw != null) {
            final int fullId = raw[index];
            raw[index] = (char) (fullId & 0xFFF0 | data);
        }
        if (this.palette != null && this.encodedData != null) {
            final char fullId = this.palette.getKey(this.encodedData.getAt(index));
            if ((fullId & 0xF) != data) {
                this.setPaletteFullBlock(index, (char) (fullId & 0xFFF0 | data));
            }
        } else {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    public int getFullBlock(final int x, final int y, final int z) {
        return this.getFullBlock(this.getIndex(x, y, z));
    }

    public void setFullBlock(final int x, final int y, final int z, final int value) {
        this.setFullBlock(this.getIndex(x, y, z), (char) value);
    }

    public int getAndSetFullBlock(final int x, final int y, final int z, final int value) {
        return this.getAndSetFullBlock(this.getIndex(x, y, z), (char) value);
    }

    public synchronized boolean compress() {
        final char[] raw = this.rawData;
        if (raw != null) {
            char unique = 0;

            final boolean[] countTable = ThreadCache.boolCache4096.get();
            final char[] mapFullTable = ThreadCache.charCache4096.get();
            final char[] mapBitTable = ThreadCache.charCache4096v2.get();
            Arrays.fill(countTable, false);
            for (final char c : raw) {
                if (!countTable[c]) {
                    mapBitTable[unique] = c;
                    countTable[c] = true;
                    unique++;
                }
            }

            final char[] keys = Arrays.copyOfRange(mapBitTable, 0, unique);
            if (keys.length > 1) {
                Arrays.sort(keys);
                for (char c = 0; c < keys.length; c++) {
                    mapFullTable[keys[c]] = c;
                }
            } else {
                mapFullTable[keys[0]] = 0;
            }

            final CharPalette palette = new CharPalette();
            palette.set(keys);

            final int bits = MathHelper.log2(unique - 1);
            final BitArray4096 encodedData = new BitArray4096(bits);

            for (int i = 0; i < raw.length; i++) {
                mapBitTable[i] = mapFullTable[raw[i]];
            }

            encodedData.fromRaw(mapBitTable);

            this.palette = palette;
            this.encodedData = encodedData;
            this.rawData = null;
            return true;
        }
        return false;
    }

    @Override
    public synchronized BlockDataPalette clone() {
        final char[] raw = this.getRaw();
        return new BlockDataPalette(raw.clone());
    }

    private char[] getCachedRaw() {
        final char[] raw = this.rawData;
        if (raw != null) {
            return raw;
        } else if (!Server.getInstance().isPrimaryThread()) {
            return this.getRaw();
        }
        return this.rawData;
    }

    private int getIndex(final int x, final int y, final int z) {
        return (x << 8) + (z << 4) + y; // XZY = Bedrock format
    }

    private int getAndSetFullBlock(final int index, final char value) {
        final char[] raw = this.getCachedRaw();
        if (raw != null) {
            final char result = raw[index];
            raw[index] = value;
            return result;
        } else if (this.palette != null && this.encodedData != null) {
            final char result = this.palette.getKey(this.encodedData.getAt(index));
            if (result != value) {
                this.setPaletteFullBlock(index, value);
            }
            return result;
        } else {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    private int getFullBlock(final int index) {
        final char[] raw = this.getCachedRaw();
        if (raw != null) {
            return raw[index];
        } else if (this.palette != null && this.encodedData != null) {
            return this.palette.getKey(this.encodedData.getAt(index));
        } else {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    private void setFullBlock(final int index, final char value) {
        final char[] raw = this.getCachedRaw();
        if (raw != null) {
            raw[index] = value;
        } else if (!this.setPaletteFullBlock(index, value)) {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    private synchronized boolean setPaletteFullBlock(final int index, final char value) {
        final CharPalette palette = this.palette;
        final BitArray4096 encodedData = this.encodedData;
        if (palette != null && encodedData != null) {
            final char encodedValue = palette.getValue(value);
            if (encodedValue != Character.MAX_VALUE) {
                encodedData.setAt(index, encodedValue);
            } else {
                final char[] raw = encodedData.toRaw();
                for (int i = 0; i < BlockDataPalette.BLOCK_SIZE; i++) {
                    raw[i] = palette.getKey(raw[i]);
                }
                raw[index] = value;
                this.rawData = raw;
                this.encodedData = null;
                this.palette = null;
            }
            return true;
        }
        return false;
    }

}
