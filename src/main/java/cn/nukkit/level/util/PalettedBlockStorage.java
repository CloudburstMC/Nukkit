package cn.nukkit.level.util;

import cn.nukkit.Server;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.utils.BinaryStream;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class PalettedBlockStorage {

    private static final int SIZE = 4096;

    private final IntList palette;
    private BitArray bitArray;

    public static PalettedBlockStorage createFromBlockPalette() {
        return createFromBlockPalette(BitArrayVersion.V2);
    }

    public static PalettedBlockStorage createFromBlockPalette(BitArrayVersion version) {
        int runtimeId = GlobalBlockPalette.getOrCreateRuntimeId(0);
        return new PalettedBlockStorage(version, runtimeId);
    }

    public static PalettedBlockStorage createWithDefaultState(int defaultState) {
        return createWithDefaultState(BitArrayVersion.V2, defaultState);
    }

    public static PalettedBlockStorage createWithDefaultState(BitArrayVersion version, int defaultState) {
        return new PalettedBlockStorage(version, defaultState);
    }

    public static PalettedBlockStorage createFromBitArray(BitArray bitArray, IntList palette) {
        return new PalettedBlockStorage(bitArray, palette);
    }

    private PalettedBlockStorage(BitArrayVersion version, int defaultState) {
        this.bitArray = version.createPalette(SIZE);
        this.palette = new IntArrayList(16);
        this.palette.add(defaultState);
    }

    private PalettedBlockStorage(BitArray bitArray, IntList palette) {
        this.palette = palette;
        this.bitArray = bitArray;
    }

    private int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.getId() << 1) | (runtime ? 1 : 0);
    }

    private static BitArrayVersion getVersionFromHeader(byte header) {
        return BitArrayVersion.get(header >> 1, true);
    }

    private int getIndex(int x, int y, int z) {
        return (x << 8) | (z << 4) | y;
    }

    public void setBlock(int x, int y, int z, int runtimeId) {
        this.setBlock(this.getIndex(x, y, z), runtimeId);
    }

    public int getBlock(int x, int y, int z) {
        int index = this.getIndex(x, y, z);
        return this.palette.getInt(this.bitArray.get(index));
    }

    public void setBlock(int index, int runtimeId) {
        try {
            int id = this.idFor(runtimeId);
            this.bitArray.set(index, id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set block runtime ID: " + runtimeId + ", palette: " + palette, e);
        }
    }

    public void readFromStorage(ByteBuf buffer) {
        BitArrayVersion version = getVersionFromHeader(buffer.readByte());
        this.palette.clear();
        int paletteSize = 1;

        if (version == BitArrayVersion.V0) {
            this.bitArray = version.createPalette(SIZE, null);
        } else {
            int expectedWordCount = version.getWordsForSize(SIZE);
            int[] words = new int[expectedWordCount];
            for (int i = 0; i < expectedWordCount; i++) {
                words[i] = buffer.readIntLE();
            }
            paletteSize = buffer.readIntLE();
            this.bitArray = version.createPalette(SIZE, words);
        }

        if (!(version.getMaxEntryValue() >= paletteSize - 1)) {
            throw new IllegalArgumentException(String.format("Palette (version " + version.name() + ") is too large. Max size %s. Actual size %s", version.getMaxEntryValue(), paletteSize));
        }

        for (int i = 0; i < paletteSize; i++) {
            int id = buffer.readIntLE();
            this.palette.add(id);
            if (id < 0) {
                Server.getInstance().getLogger().warning("Negative biome ID " + id);
            }
        }
    }

    public void writeToStorage(ByteBuf buffer) {
        int paletteSize = this.palette.size();
        BitArrayVersion version = paletteSize <= 1 ? BitArrayVersion.V0 : this.bitArray.getVersion();
        buffer.writeByte(getPaletteHeader(version, false));

        if (version != BitArrayVersion.V0) {
            for (int word : this.bitArray.getWords()) {
                buffer.writeIntLE(word);
            }
            buffer.writeIntLE(paletteSize);
        }

        for (int runtimeId : this.palette) {
            buffer.writeIntLE(runtimeId);
        }
    }

    public void writeTo(BinaryStream stream) {
        this.writeTo(stream, i -> i);
    }

    public void writeTo(BinaryStream stream, Int2IntFunction mapper) {
        stream.putByte((byte) getPaletteHeader(this.bitArray.getVersion(), true));
        if (this.bitArray.getVersion() != BitArrayVersion.V0) {
            for (int word : this.bitArray.getWords()) {
                stream.putLInt(word);
            }
            stream.putVarInt(this.palette.size());
        }

        for (int runtimeId : this.palette) {
            stream.putVarInt(mapper.get(runtimeId));
        }
    }

    private void onResize(BitArrayVersion version) {
        BitArray newBitArray = version.createPalette();

        for (int i = 0; i < SIZE; i++) {
            newBitArray.set(i, this.bitArray.get(i));
        }
        this.bitArray = newBitArray;
    }

    private int idFor(int runtimeId) {
        int index = this.palette.indexOf(runtimeId);
        if (index != -1) {
            return index;
        }

        index = this.palette.size();
        BitArrayVersion version = this.bitArray.getVersion();
        if (index > version.getMaxEntryValue()) {
            BitArrayVersion next = version.next();
            if (next != null) {
                this.onResize(next);
            }
        }
        this.palette.add(runtimeId);
        return index;
    }

    public boolean isEmpty() {
        if (this.palette.size() == 1) {
            return true;
        }
        for (int word : this.bitArray.getWords()) {
            if (Integer.toUnsignedLong(word) != 0L) {
                return false;
            }
        }
        return true;
    }

    public PalettedBlockStorage copy() {
        return new PalettedBlockStorage(this.bitArray.copy(), new IntArrayList(this.palette));
    }
}
