package cn.nukkit.level.util;

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

    public PalettedBlockStorage(final BitArrayVersion version) {
        this.bitArray = version.createPalette(PalettedBlockStorage.SIZE);
        this.palette = new IntArrayList(16);
        this.palette.add(0); // Air is at the start of every palette.
    }

    private PalettedBlockStorage(final BitArray bitArray, final IntList palette) {
        this.palette = palette;
        this.bitArray = bitArray;
    }

    public void setBlock(final int index, final int runtimeId) {
        try {
            final int id = this.idFor(runtimeId);
            this.bitArray.set(index, id);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set block runtime ID: " + runtimeId + ", palette: " + this.palette, e);
        }
    }

    public void writeTo(final BinaryStream stream) {
        stream.putByte((byte) this.getPaletteHeader(this.bitArray.getVersion(), true));

        for (final int word : this.bitArray.getWords()) {
            stream.putLInt(word);
        }

        stream.putVarInt(this.palette.size());
        this.palette.forEach((IntConsumer) stream::putVarInt);
    }

    public boolean isEmpty() {
        if (this.palette.size() == 1) {
            return true;
        }
        for (final int word : this.bitArray.getWords()) {
            if (Integer.toUnsignedLong(word) != 0L) {
                return false;
            }
        }
        return true;
    }

    public PalettedBlockStorage copy() {
        return new PalettedBlockStorage(this.bitArray.copy(), new IntArrayList(this.palette));
    }

    private int getPaletteHeader(final BitArrayVersion version, final boolean runtime) {
        return version.getId() << 1 | (runtime ? 1 : 0);
    }

    private void onResize(final BitArrayVersion version) {
        final BitArray newBitArray = version.createPalette(PalettedBlockStorage.SIZE);

        for (int i = 0; i < PalettedBlockStorage.SIZE; i++) {
            newBitArray.set(i, this.bitArray.get(i));
        }
        this.bitArray = newBitArray;
    }

    private int idFor(final int runtimeId) {
        int index = this.palette.indexOf(runtimeId);
        if (index != -1) {
            return index;
        }

        index = this.palette.size();
        final BitArrayVersion version = this.bitArray.getVersion();
        if (index > version.getMaxEntryValue()) {
            final BitArrayVersion next = version.next();
            if (next != null) {
                this.onResize(next);
            }
        }
        this.palette.add(runtimeId);
        return index;
    }

}
