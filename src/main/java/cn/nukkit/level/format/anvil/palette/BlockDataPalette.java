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
        this(new char[BLOCK_SIZE]);
    }

    public BlockDataPalette(char[] rawData) {
        Preconditions.checkArgument(rawData.length == BLOCK_SIZE, "Data is not 4096");
        this.rawData = rawData;
    }

    private char[] getCachedRaw() {
        char[] raw = rawData;
        if (raw != null) {
            return raw;
        } else if (!Server.getInstance().isPrimaryThread()) {
            return getRaw();
        }
        return rawData;
    }

    public synchronized char[] getRaw() {
        CharPalette palette = this.palette;
        BitArray4096 encodedData = this.encodedData;
        this.encodedData = null;
        this.palette = null;

        char[] raw = rawData;
        if (raw == null && palette != null) {
            if (encodedData != null) {
                raw = encodedData.toRaw();
            } else {
                raw = new char[BLOCK_SIZE];
            }
            for (int i = 0; i < BLOCK_SIZE; i++) {
                raw[i] = palette.getKey(raw[i]);
            }
        } else {
            raw = new char[BLOCK_SIZE];
        }
        rawData = raw;
        return rawData;
    }

    private int getIndex(int x, int y, int z) {
        return (x << 8) + (z << 4) + y; // XZY = Bedrock format
    }

    public int getBlockData(int x, int y, int z) {
        return getFullBlock(x, y, z) & 0xF;
    }

    public int getBlockId(int x, int y, int z) {
        return getFullBlock(x, y, z) >> 4;
    }

    public void setBlockId(int x, int y, int z, int id) {
        setFullBlock(x, y, z, (char) (id << 4));
    }

    public synchronized void setBlockData(int x, int y, int z, int data) {
        int index = getIndex(x, y, z);
        char[] raw = getCachedRaw();

        if (raw != null) {
            int fullId = raw[index];
            raw[index] = (char) ((fullId & 0xFFF0) | data);
        } if (palette != null && encodedData != null) {
            char fullId = palette.getKey(encodedData.getAt(index));
            if ((fullId & 0xF) != data) {
                setPaletteFullBlock(index, (char) ((fullId & 0xFFF0) | data));
            }
        } else {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(getIndex(x, y, z));
    }

    public void setFullBlock(int x, int y, int z, int value) {
        this.setFullBlock(getIndex(x, y, z), (char) value);
    }

    public int getAndSetFullBlock(int x, int y, int z, int value) {
        return getAndSetFullBlock(getIndex(x, y, z), (char) value);
    }

    private int getAndSetFullBlock(int index, char value) {
        char[] raw = getCachedRaw();
        if (raw != null) {
            char result = raw[index];
            raw[index] = value;
            return result;
        } else if (palette != null && encodedData != null) {
            char result = palette.getKey(encodedData.getAt(index));
            if (result != value) {
                setPaletteFullBlock(index, value);
            }
            return result;
        } else {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    private int getFullBlock(int index) {
        char[] raw = getCachedRaw();
        if (raw != null) {
            return raw[index];
        } else if (palette != null && encodedData != null) {
            return palette.getKey(encodedData.getAt(index));
        } else {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    private void setFullBlock(int index, char value) {
        char[] raw = getCachedRaw();
        if (raw != null) {
            raw[index] = value;
        } else if (!setPaletteFullBlock(index, value)) {
            throw new IllegalStateException("Raw data and pallete was null");
        }
    }

    private synchronized boolean setPaletteFullBlock(int index, char value) {
        CharPalette palette = this.palette;
        BitArray4096 encodedData = this.encodedData;
        if (palette != null && encodedData != null) {
            char encodedValue = palette.getValue(value);
            if (encodedValue != Character.MAX_VALUE) {
                encodedData.setAt(index, encodedValue);
            } else {
                char[] raw = encodedData.toRaw();
                for (int i = 0; i < BLOCK_SIZE; i++) {
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

    public synchronized boolean compress() {
        char[] raw = rawData;
        if (raw != null) {
            char unique = 0;

            boolean[] countTable = ThreadCache.boolCache4096.get();
            char[] mapFullTable = ThreadCache.charCache4096.get();
            char[] mapBitTable = ThreadCache.charCache4096v2.get();
            Arrays.fill(countTable, false);
            for (char c : raw) {
                if (!countTable[c]) {
                    mapBitTable[unique] = c;
                    countTable[c] = true;
                    unique++;
                }
            }

            char[] keys = Arrays.copyOfRange(mapBitTable, 0, unique);
            if (keys.length > 1) {
                Arrays.sort(keys);
                for (char c = 0; c < keys.length; c++) {
                    mapFullTable[keys[c]] = c;
                }
            } else {
                mapFullTable[keys[0]] = 0;
            }

            CharPalette palette = new CharPalette();
            palette.set(keys);

            int bits = MathHelper.log2(unique - 1);
            BitArray4096 encodedData = new BitArray4096(bits);

            for (int i = 0; i < raw.length; i++) {
                mapBitTable[i] = mapFullTable[raw[i]];
            }

            encodedData.fromRaw(mapBitTable);

            this.palette = palette;
            this.encodedData = encodedData;
            rawData = null;
            return true;
        }
        return false;
    }

    public synchronized BlockDataPalette clone() {
        char[] raw = getRaw();
        return new BlockDataPalette(raw.clone());
    }
}
