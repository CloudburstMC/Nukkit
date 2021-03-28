package cn.nukkit.level.util;

import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.utils.BinaryStream;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.function.IntConsumer;

public class PalettedBlockStorage {

    private static final int SIZE = 4096;

    private final IntList palette;
    private BitArray bitArray;

    public PalettedBlockStorage() {
        this(BitArrayVersion.V2);
    }

    public PalettedBlockStorage(BitArrayVersion version) {
        this.bitArray = version.createPalette(SIZE);
        this.palette = new IntArrayList(16);
        this.palette.add(GlobalBlockPalette.getOrCreateRuntimeId(0)); // Air is at the start of every palette.
    }

    private PalettedBlockStorage(BitArray bitArray, IntList palette) {
        this.palette = palette;
        this.bitArray = bitArray;
    }

    private int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.getId() << 1) | (runtime ? 1 : 0);
    }

    public void setBlock(int index, int runtimeId) {
        try {
            int id = this.idFor(runtimeId);
            this.bitArray.set(index, id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set block runtime ID: " + runtimeId + ", palette: " + palette, e);
        }
    }

    public void writeTo(BinaryStream stream) {
        stream.putByte((byte) getPaletteHeader(bitArray.getVersion(), true));

        for (int word : bitArray.getWords()) {
            stream.putLInt(word);
        }

        stream.putVarInt(palette.size());
        palette.forEach((IntConsumer) stream::putVarInt);
    }

    private void onResize(BitArrayVersion version) {
        BitArray newBitArray = version.createPalette(SIZE);

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
