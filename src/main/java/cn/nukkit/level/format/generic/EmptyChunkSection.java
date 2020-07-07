package cn.nukkit.level.format.generic;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EmptyChunkSection implements ChunkSection {
    @SuppressWarnings("java:S2386")
    public static final EmptyChunkSection[] EMPTY = new EmptyChunkSection[16];
    private static final String MODIFICATION_ERROR_MESSAGE = "Tried to modify an empty Chunk";

    static {
        for (int y = 0; y < EMPTY.length; y++) {
            EMPTY[y] = new EmptyChunkSection(y);
        }
    }
    
    private static final byte[] EMPTY_2KB = new byte[2048];
    public static final byte[] EMPTY_LIGHT_ARR = EMPTY_2KB;
    public static final byte[] EMPTY_SKY_LIGHT_ARR = new byte[2048];
    static {
        Arrays.fill(EMPTY_SKY_LIGHT_ARR, (byte) 255);
    }
    
    public static final byte[] EMPTY_ID_ARRAY = new byte[4096];
    public static final byte[] EMPTY_DATA_ARRAY = EMPTY_2KB;
    private static final byte[] EMPTY_CHUNK_DATA;
    static {
        BinaryStream stream = new BinaryStream();
        stream.putByte((byte) cn.nukkit.level.format.anvil.ChunkSection.STREAM_STORAGE_VERSION);
        stream.putVarInt(0);
        EMPTY_CHUNK_DATA = stream.getBuffer();
    }

    private final int y;

    public EmptyChunkSection(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public final int getBlockId(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return 0;
    }

    @Nonnull
    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return BlockState.AIR;
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Nonnull
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        if (block.getId() != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return Block.get(0);
    }

    @Nonnull
    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        if (block.getId() != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return Block.get(0);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {
        if (id != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state) {
        if (!state.equals(BlockState.AIR)) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public byte[] getSkyLightArray() {
        return EMPTY_SKY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        return EMPTY_LIGHT_ARR;
    }

    @Override
    public final void setBlockId(int x, int y, int z, int id) {
        if (id != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public final int getBlockData(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        if (data != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        if (data != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        if (fullId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        if (fullId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        if (level != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 15;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        if (level != 15) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void writeTo(@Nonnull BinaryStream stream) {
        stream.putByteArray(EMPTY_CHUNK_DATA);
    }
    
    @Override
    public byte[] getBytes() {
        return EMPTY_CHUNK_DATA;
    }
    
    @Override
    public int getMaximumLayer() {
        return 0;
    }
    
    @Override
    public CompoundTag toNBT() {
        return null;
    }

    @Override
    public EmptyChunkSection copy() {
        return this;
    }
}
