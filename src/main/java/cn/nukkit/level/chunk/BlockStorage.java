package cn.nukkit.level.chunk;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.chunk.bitarray.BitArray;
import cn.nukkit.level.chunk.bitarray.BitArrayVersion;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Binary;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.function.IntConsumer;

public class BlockStorage {

    private static final int SIZE = 4096;

    private final IntList palette;
    private BitArray bitArray;

    public BlockStorage() {
        this(BitArrayVersion.V2);
    }

    public BlockStorage(BitArrayVersion version) {
        this.bitArray = version.createPalette(SIZE);
        this.palette = new IntArrayList(16);
        this.palette.add(0); // Air is at the start of every palette.
    }

    private BlockStorage(BitArray bitArray, IntList palette) {
        this.palette = palette;
        this.bitArray = bitArray;
    }

    private static int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.getId() << 1) | (runtime ? 1 : 0);
    }

    private static BitArrayVersion getVersionFromHeader(byte header) {
        return BitArrayVersion.get(header >> 1, true);
    }

    public synchronized Block getBlock(int index) {
        return this.blockFor(this.bitArray.get(index)).clone();
    }

    public synchronized void setBlock(int index, Block block) {
        try {
            int idx = this.idFor(GlobalBlockPalette.getRuntimeId(block));
            this.bitArray.set(index, idx);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set block: " + block + ", palette: " + palette, e);
        }
    }

    public synchronized int getBlockId(int index) {
        return this.blockFor(this.bitArray.get(index)).getId();
    }

    public synchronized void setBlockId(int index, int blockId) {
        try {
            int idx = this.idFor(GlobalBlockPalette.getRuntimeId(blockId, 0));
            this.bitArray.set(index, idx);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set full block ID: " + blockId + ", palette: " + palette, e);
        }
    }

    public synchronized int getBlockData(int index) {
        return this.blockFor(this.bitArray.get(index)).getDamage();
    }

    public synchronized void setBlockData(int index, int blockData) {
        try {
            int id = this.getBlockId(index);
            int idx = this.idFor(GlobalBlockPalette.getRuntimeId(id, blockData));
            this.bitArray.set(index, idx);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set full block data: " + blockData + ", palette: " + palette, e);
        }
    }

    public synchronized void writeToNetwork(ByteBuf buffer) {
        buffer.writeByte(getPaletteHeader(bitArray.getVersion(), true));

        for (int word : bitArray.getWords()) {
            buffer.writeIntLE(word);
        }

        Binary.writeVarInt(buffer, palette.size());
        palette.forEach((IntConsumer) id -> Binary.writeVarInt(buffer, id));
    }

    public synchronized void writeToStorage(ByteBuf buffer) {
        buffer.writeByte(getPaletteHeader(bitArray.getVersion(), false));
        for (int word : bitArray.getWords()) {
            buffer.writeIntLE(word);
        }

        buffer.writeIntLE(this.palette.size());

        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer)) {
            for (int runtimeId : palette.toIntArray()) {
                Block block = GlobalBlockPalette.getBlock(runtimeId);
                String name = GlobalBlockPalette.getNameFromLegacyId(block.getId());
                CompoundTag tag = new CompoundTag();
                tag.putString("name", name);
                tag.putShort("val", block.getDamage());

                NBTIO.write(tag, stream, ByteOrder.LITTLE_ENDIAN);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void readFromStorage(ByteBuf buffer) {
        BitArrayVersion version = getVersionFromHeader(buffer.readByte());

        int expectedWordCount = version.getWordsForSize(SIZE);
        int[] words = new int[expectedWordCount];
        for (int i = 0; i < expectedWordCount; i++) {
            words[i] = buffer.readIntLE();
        }
        this.bitArray = version.createPalette(SIZE, words);

        this.palette.clear();
        int paletteSize = buffer.readIntLE();

        Preconditions.checkArgument(version.getMaxEntryValue() >= paletteSize - 1,
                "Palette is too large. Max size %s. Actual size %s", version.getMaxEntryValue(),
                paletteSize);

        try (ByteBufInputStream stream = new ByteBufInputStream(buffer)) {
            for (int i = 0; i < paletteSize; i++) {
                CompoundTag tag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
                int id = GlobalBlockPalette.getLegacyIdFromName(tag.getString("name"));
                int data = tag.getShort("val");

                int runtimeId = GlobalBlockPalette.getRuntimeId(id, data);
                if (palette.indexOf(runtimeId) != -1) {
                    throw new IllegalArgumentException("Palette contains same block state twice!");
                }

                this.palette.add(runtimeId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void onResize(BitArrayVersion version) {
        BitArray newBitArray = version.createPalette(SIZE);

        for (int i = 0; i < SIZE; i++) {
            newBitArray.set(i, this.bitArray.get(i));
        }
        this.bitArray = newBitArray;
    }

    private synchronized int idFor(int runtimeId) {
        ;
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

    private synchronized Block blockFor(int index) {
        int runtimeId = this.palette.getInt(index);
        return GlobalBlockPalette.getBlock(runtimeId);
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

    public BlockStorage copy() {
        return new BlockStorage(this.bitArray.copy(), new IntArrayList(this.palette));
    }
}
