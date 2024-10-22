package cn.nukkit.level.format.leveldb.structure;

import cn.nukkit.Nukkit;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.leveldb.BlockStateMapping;
import cn.nukkit.level.util.BitArray;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.MainLogger;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class StateBlockStorage {
    private static final Logger log = LogManager.getLogger("LevelDB-Logger");

    private static final int SECTION_SIZE = 4096;

    private final List<BlockStateSnapshot> palette;
    private BitArray bitArray;

    public StateBlockStorage() {
        this(BitArrayVersion.V2);
    }

    public StateBlockStorage(BitArrayVersion version) {
        this.bitArray = version.createPalette();
        this.palette = new ObjectArrayList<>(16);
        // Air is at the beginning of each palette
        this.palette.add(BlockStateMapping.get().getState(0, 0));
    }

    public StateBlockStorage(BitArray bitArray, List<BlockStateSnapshot> palette) {
        this.palette = palette;
        this.bitArray = bitArray;
    }

    private int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.getId() << 1) | (runtime ? 1 : 0);
    }

    private static BitArrayVersion getVersionFromHeader(byte header) {
        return BitArrayVersion.get(header >> 1, true);
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

        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(stream)) {
            for (BlockStateSnapshot state : this.palette) {
                nbtOutputStream.writeTag(state.getVanillaState());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readFromStorage(ByteBuf buffer, ChunkBuilder chunkBuilder) {
        BitArrayVersion version = getVersionFromHeader(buffer.readByte());
        this.palette.clear();
        int paletteSize = 1;

        if (version == BitArrayVersion.V0) {
            this.bitArray = version.createPalette(SECTION_SIZE, null);
        } else {
            int expectedWordCount = version.getWordsForSize(SECTION_SIZE);
            int[] words = new int[expectedWordCount];
            for (int i = 0; i < expectedWordCount; i++) {
                words[i] = buffer.readIntLE();
            }
            paletteSize = buffer.readIntLE();
            this.bitArray = version.createPalette(SECTION_SIZE, words);
        }

        if (version.getMaxEntryValue() < paletteSize - 1) {
            throw new IllegalArgumentException(
                    chunkBuilder.debugString() + " Palette (version " + version.name() + ") is too large. Max size " + version.getMaxEntryValue() + ". Actual size " + paletteSize
            );
        }

        try (ByteBufInputStream stream = new ByteBufInputStream(buffer);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(stream)) {
            for (int i = 0; i < paletteSize; i++) {
                try {
                    NbtMap state = (NbtMap) nbtInputStream.readTag();
                    //noinspection ResultOfMethodCallIgnored
                    state.hashCode(); // cache hashCode

                    BlockStateSnapshot blockState = BlockStateMapping.get().getStateUnsafe(state);
                    if (blockState == null) {
                        NbtMap updatedState = BlockStateMapping.get().updateVanillaState(state);
                        blockState = BlockStateMapping.get().getUpdatedOrCustom(state, updatedState);
                        if (!blockState.isCustom()) {
                            if (Nukkit.DEBUG > 1) log.info("[{}] Updated unmapped block state: {} => {}", chunkBuilder.debugString(), state, blockState.getVanillaState());
                            chunkBuilder.dirty();
                        }

                        if (Nukkit.DEBUG > 1 && blockState.getRuntimeId() == BlockStateMapping.get().getDefaultRuntimeId()) {
                            log.info("[{}] Chunk contains unknown block {}  => {}", chunkBuilder.debugString(), state, updatedState);
                        }
                    }

                    if (Nukkit.DEBUG > 1 && this.palette.contains(blockState)) {
                        log.info("[{}] Palette contains block state twice: {}", chunkBuilder.debugString(), state);
                    }
                    this.palette.add(blockState);
                } catch (Exception e) {
                    MainLogger.getLogger().error("[" + chunkBuilder.debugString() + "] Unable to deserialize chunk block state", e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBlockState(int index, NbtMap state) {
        BlockStateSnapshot blockState = BlockStateMapping.get().getStateUnsafe(state);
        if (blockState == null) {
            blockState = BlockStateMapping.get().updateStateUnsafe(state);
        }
        this.setBlockStateUnsafe(index, blockState);
    }

    public void setBlockStateUnsafe(int index, BlockStateSnapshot state) {
        try {
            int id = this.idFor(state);
            this.bitArray.set(index, id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to set block state: " + state + ", palette: " + palette, e);
        }
    }

    public BlockStateSnapshot getBlockState(int x, int y, int z) {
        int index = ChunkBuilder.getSectionIndex(x, y, z);
        return this.stateFor(this.bitArray.get(index));
    }

    public BlockStateSnapshot getBlockState(int index) {
        return this.stateFor(this.bitArray.get(index));
    }

    public int getBlockData(int x, int y, int z) {
        return this.getBlockState(x, y, z).getLegacyData();
    }

    public int getBlockId(int x, int y, int z) {
        return this.getBlockState(x, y, z).getLegacyId();
    }

    public void setBlockId(int x, int y, int z, int id) {
        int index = ChunkBuilder.getSectionIndex(x, y, z);
        this.setBlockStateUnsafe(index, BlockStateMapping.get().getState(id, 0));
    }

    public void setBlockData(int x, int y, int z, int data) {
        int index = ChunkBuilder.getSectionIndex(x, y, z);
        this.setBlockStateUnsafe(index, BlockStateMapping.get().getState(this.getBlockId(x, y, z), data));
    }

    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(ChunkBuilder.getSectionIndex(x, y, z));
    }

    public void setFullBlock(int x, int y, int z, int value) {
        this.setFullBlock(ChunkBuilder.getSectionIndex(x, y, z), value);
    }

    public int getAndSetFullBlock(int x, int y, int z, int value) {
        return getAndSetFullBlock(ChunkBuilder.getSectionIndex(x, y, z), value);
    }

    private int getAndSetFullBlock(int index, int value) {
        BlockStateSnapshot state = this.getBlockState(index);
        int newBlock = value >> Block.DATA_BITS;
        int newData = value & Block.DATA_MASK;
        this.setBlockStateUnsafe(index, BlockStateMapping.get().getState(newBlock, newData));
        return (state.getLegacyId() << Block.DATA_BITS) | state.getLegacyData();
    }

    private int getFullBlock(int index) {
        BlockStateSnapshot state = this.getBlockState(index);
        return (state.getLegacyId() << Block.DATA_BITS) | state.getLegacyData();
    }

    public void setFullBlock(int index, int value) {
        int block = value >> Block.DATA_BITS;
        int data = value & Block.DATA_MASK;
        this.setBlockStateUnsafe(index, BlockStateMapping.get().getState(block, data));
    }

    public void writeTo(BinaryStream stream) {
        BitArray bitArray = this.bitArray;

        stream.putByte((byte) this.getPaletteHeader(bitArray.getVersion(), true));
        if (bitArray.getVersion() != BitArrayVersion.V0) {
            for (int word : bitArray.getWords()) {
                stream.putLInt(word);
            }
            stream.putVarInt(this.palette.size());
        }

        for (BlockStateSnapshot state : this.palette) {
            stream.putVarInt(state.getRuntimeIdNetworkProtocol());
        }
    }

    private void onResize(BitArrayVersion version) {
        BitArray newBitArray = version.createPalette();
        for (int i = 0; i < SECTION_SIZE; i++) {
            newBitArray.set(i, this.bitArray.get(i));
        }
        this.bitArray = newBitArray;
    }

    private int idFor(BlockStateSnapshot state) {
        int index = this.palette.indexOf(state);
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
        this.palette.add(state);
        return index;
    }

    private BlockStateSnapshot stateFor(int index) {
        return this.palette.get(index);
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

    public StateBlockStorage copy() {
        return new StateBlockStorage(this.bitArray.copy(), new ObjectArrayList<>(this.palette));
    }
}
