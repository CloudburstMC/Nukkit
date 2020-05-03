package cn.nukkit.level.chunk;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.bitarray.BitArray;
import cn.nukkit.level.chunk.bitarray.BitArrayVersion;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.IOException;
import java.util.function.IntConsumer;

import static com.google.common.base.Preconditions.checkArgument;

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

    private int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.getId() << 1) | (runtime ? 1 : 0);
    }

    private static BitArrayVersion getVersionFromHeader(byte header) {
        return BitArrayVersion.get(header >> 1, true);
    }

    public Block getBlock(int index) {
        return this.blockFor(this.bitArray.get(index)).clone();
    }

    public void setBlock(int index, Block block) {
        try {
            int idx = this.idFor(BlockRegistry.get().getRuntimeId(block));
            this.bitArray.set(index, idx);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set block: " + block + ", palette: " + palette, e);
        }
    }

    public Identifier getBlockId(int index) {
        return this.blockFor(this.bitArray.get(index)).getId();
    }

    public void setBlockId(int index, int blockId) {
        try {
            int idx = this.idFor(BlockRegistry.get().getRuntimeId(blockId, 0));
            this.bitArray.set(index, idx);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set full block ID: " + blockId + ", palette: " + palette, e);
        }
    }

    public int getBlockRuntimeIdUnsafe(int index)    {
        return this.palette.getInt(this.bitArray.get(index));
    }

    public void setBlockRuntimeIdUnsafe(int index, int runtimeId)    {
        try {
            BlockRegistry.get().getBlock(runtimeId); //this will throw RegistryException if the runtimeId is not registered
            int idx = this.idFor(runtimeId); //need to do this separately since bitArray can change, and is apparently dereferenced first
            this.bitArray.set(index, idx);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set block runtime ID: " + runtimeId + ", palette: " + palette, e);
        }
    }

    public int getBlockData(int index) {
        return this.blockFor(this.bitArray.get(index)).getMeta();
    }

    public void setBlockData(int index, int blockData) {
        try {
            Identifier id = this.getBlockId(index);
            int idx = this.idFor(BlockRegistry.get().getRuntimeId(id, blockData));
            this.bitArray.set(index, idx);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set full block data: " + blockData + ", palette: " + palette, e);
        }
    }

    public void writeToNetwork(ByteBuf buffer) {
        buffer.writeByte(getPaletteHeader(bitArray.getVersion(), true));

        for (int word : bitArray.getWords()) {
            buffer.writeIntLE(word);
        }

        VarInts.writeInt(buffer, palette.size());
        palette.forEach((IntConsumer) id -> VarInts.writeInt(buffer, id));
    }

    public void writeToStorage(ByteBuf buffer) {
        buffer.writeByte(getPaletteHeader(bitArray.getVersion(), false));
        for (int word : bitArray.getWords()) {
            buffer.writeIntLE(word);
        }

        buffer.writeIntLE(this.palette.size());

        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(stream)) {
            for (int runtimeId : palette.toIntArray()) {
                Block block = BlockRegistry.get().getBlock(runtimeId);

                nbtOutputStream.write(CompoundTag.builder()
                        .stringTag("name", block.getId().toString())
                        .shortTag("val", (short) block.getMeta())
                        .buildRootTag());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readFromStorage(ByteBuf buffer) {
        BitArrayVersion version = getVersionFromHeader(buffer.readByte());

        int expectedWordCount = version.getWordsForSize(SIZE);
        int[] words = new int[expectedWordCount];
        for (int i = 0; i < expectedWordCount; i++) {
            words[i] = buffer.readIntLE();
        }
        this.bitArray = version.createPalette(SIZE, words);

        this.palette.clear();
        int paletteSize = buffer.readIntLE();

        checkArgument(version.getMaxEntryValue() >= paletteSize - 1,
                "Palette is too large. Max size %s. Actual size %s", version.getMaxEntryValue(),
                paletteSize);

        try (ByteBufInputStream stream = new ByteBufInputStream(buffer);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(stream)) {
            for (int i = 0; i < paletteSize; i++) {
                CompoundTag tag = (CompoundTag) nbtInputStream.readTag();
                checkArgument(!tag.contains("states"), "Unsupported chunk version (flattened)"); // TODO: 19/04/2020 Support this

                String name = tag.getString("name");
                int id = BlockRegistry.get().getLegacyId(name);
                int data = tag.getShort("val");

                int runtimeId = BlockRegistry.get().getRuntimeId(id, data);
                checkArgument(!this.palette.contains(runtimeId),
                        "Palette contains block state (%s) twice!", name + ":" + data);
                this.palette.add(runtimeId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private Block blockFor(int index) {
        int runtimeId = this.palette.getInt(index);
        return BlockRegistry.get().getBlock(runtimeId);
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
