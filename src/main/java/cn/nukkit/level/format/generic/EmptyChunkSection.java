package cn.nukkit.level.format.generic;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EmptyChunkSection implements ChunkSection {

    public static final EmptyChunkSection[] EMPTY = new EmptyChunkSection[16];

    private static final PalettedBlockStorage EMPTY_STORAGE = new PalettedBlockStorage(BitArrayVersion.V1);

    public static byte[] EMPTY_LIGHT_ARR = new byte[2048];

    public static byte[] EMPTY_SKY_LIGHT_ARR = new byte[2048];

    static {
        for (int y = 0; y < EmptyChunkSection.EMPTY.length; y++) {
            EmptyChunkSection.EMPTY[y] = new EmptyChunkSection(y);
        }
    }

    static {
        Arrays.fill(EmptyChunkSection.EMPTY_SKY_LIGHT_ARR, (byte) 255);
    }

    private final int y;

    public EmptyChunkSection(final int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public final int getBlockId(final int x, final int y, final int z) {
        return 0;
    }

    @Override
    public final void setBlockId(final int x, final int y, final int z, final int id) throws ChunkException {
        if (id != 0) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
    }

    @Override
    public final int getBlockData(final int x, final int y, final int z) {
        return 0;
    }

    @Override
    public void setBlockData(final int x, final int y, final int z, final int data) throws ChunkException {
        if (data != 0) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
    }

    @Override
    public int getFullBlock(final int x, final int y, final int z) throws ChunkException {
        return 0;
    }

    @Override
    public Block getAndSetBlock(final int x, final int y, final int z, final Block block) {
        if (block.getId() != 0) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
        return Block.get(0);
    }

    @Override
    public boolean setFullBlockId(final int x, final int y, final int z, final int fullId) {
        if (fullId != 0) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
        return false;
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId) throws ChunkException {
        if (blockId != 0) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
        return false;
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId, final int meta) throws ChunkException {
        if (blockId != 0) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
        return false;
    }

    @Override
    public int getBlockSkyLight(final int x, final int y, final int z) {
        return 15;
    }

    @Override
    public void setBlockSkyLight(final int x, final int y, final int z, final int level) throws ChunkException {
        if (level != 15) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
    }

    @Override
    public int getBlockLight(final int x, final int y, final int z) {
        return 0;
    }

    @Override
    public void setBlockLight(final int x, final int y, final int z, final int level) throws ChunkException {
        if (level != 0) {
            throw new ChunkException("Tried to modify an empty WoolChunk");
        }
    }

    @Override
    public byte[] getIdArray() {
        return new byte[4096];
    }

    @Override
    public byte[] getDataArray() {
        return new byte[2048];
    }

    @Override
    public byte[] getSkyLightArray() {
        return EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        return EmptyChunkSection.EMPTY_LIGHT_ARR;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void writeTo(final BinaryStream stream) {
        stream.putByte((byte) 8);
        stream.putByte((byte) 2);
        EmptyChunkSection.EMPTY_STORAGE.writeTo(stream);
        EmptyChunkSection.EMPTY_STORAGE.writeTo(stream);
    }

    @Override
    public EmptyChunkSection copy() {
        return this;
    }

    public BlockStorage getStorage() {
        return new BlockStorage(getIdArray(),new NibbleArray(getDataArray()));
    }
}
