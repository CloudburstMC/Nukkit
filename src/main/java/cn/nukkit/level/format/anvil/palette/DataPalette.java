package cn.nukkit.level.format.anvil.palette;

import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.ThreadCache;
import java.util.Arrays;

/**
 * @author https://github.com/boy0001/
 */
public final class DataPalette implements Cloneable {
    private char[] rawData;

    private BitArray encodedData;
    private CharPalette palette;

    // TODO compress unused sections
    // private byte[] compressedData;

    public DataPalette() {
        this(new char[4096]);
    }

    public DataPalette(char[] rawData) {
        this.rawData = rawData;
    }

    public synchronized char[] getRaw() {
        char[] raw = rawData;
        if (raw == null) {
            raw = encodedData.toRaw();
            for (int i = 0; i < 4096; i++) {
                raw[i] = palette.getKey(raw[i]);
            }
        }
        rawData = raw;
        encodedData = null;
        palette = null;
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

    public void setBlockData(int x, int y, int z, int data) {
        int index = getIndex(x, y, z);
        char[] raw = rawData;
        if (raw != null) {
            int fullId = raw[index];
            raw[index] = (char) ((fullId & 0xFFF0) | data);
        } else {
            char fullId = palette.getKey(encodedData.getAt(index));
            if ((fullId & 0xF) != data) {
                setPaletteFullBlock(index, (char) ((fullId & 0xFFF0) | data));
            }
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
        char[] raw = rawData;
        if (raw != null) {
            char result = raw[index];
            raw[index] = value;
            return result;
        } else {
            char fullId = palette.getKey(encodedData.getAt(index));
            if (fullId != value) {
                setPaletteFullBlock(index, value);
            }
            return fullId;
        }
    }

    private int getFullBlock(int index) {
        char[] raw = rawData;
        if (raw != null) {
            return raw[index];
        }
        return palette.getKey(encodedData.getAt(index));
    }

    private void setFullBlock(int index, char value) {
        char[] raw = rawData;
        if (raw != null) {
            raw[index] = value;
            return;
        }
        setPaletteFullBlock(index, value);
    }

    private void setPaletteFullBlock(int index, char value) {
        char encodedValue = palette.getValue(value);
        if (encodedValue != Character.MAX_VALUE) {
            encodedData.setAt(index, encodedValue);
        } else {
            synchronized (this) {
                char[] raw = encodedData.toRaw();
                for (int i = 0; i < 4096; i++) {
                    raw[i] = palette.getKey(raw[i]);
                }
                raw[index] = value;
                rawData = raw;
                encodedData = null;
                palette = null;
            }
        }
    }

    public boolean compress() {
        char[] raw = rawData;
        if (raw != null) {
            synchronized (this) {
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

                int bits = MathHelper.log2nlz(unique) + 1;
                BitArray encodedData = new BitArray(bits);

                for (int i = 0; i < raw.length; i++) {
                    raw[i] = mapFullTable[raw[i]];
                }

                encodedData.fromRaw(raw);

                this.palette = palette;
                this.encodedData = encodedData;
                rawData = null;
                return true;
            }
        }
        return false;
    }

    public synchronized DataPalette clone() {
        char[] raw = getRaw();
        return new DataPalette(raw.clone());
    }
}
