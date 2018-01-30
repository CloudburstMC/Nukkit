package cn.nukkit.level.format.generic;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.utils.ChunkException;

import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EmptyChunkSection implements ChunkSection {
    public static final EmptyChunkSection[] EMPTY = new EmptyChunkSection[16];
    static {
        for (int y = 0; y < EMPTY.length; y++) {
            EMPTY[y] = new EmptyChunkSection(y);
        }
    }
    public static byte[] EMPTY_LIGHT_ARR = new byte[2048];
    public static byte[] EMPTY_SKY_LIGHT_ARR = new byte[2048];
    static {
        Arrays.fill(EMPTY_SKY_LIGHT_ARR, (byte) 255);
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
    final public int getBlockId(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getFullBlock(int x, int y, int z) throws ChunkException {
        return 0;
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        if (block.getId() != 0) throw new ChunkException("Tried to modify an empty Chunk");
        return Block.get(0);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) throws ChunkException {
        if (blockId != 0) throw new ChunkException("Tried to modify an empty Chunk");
        return false;
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) throws ChunkException {
        if (blockId != 0) throw new ChunkException("Tried to modify an empty Chunk");
        return false;
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
        return EMPTY_SKY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        return EMPTY_LIGHT_ARR;
    }

    @Override
    final public void setBlockId(int x, int y, int z, int id) throws ChunkException {
        if (id != 0) throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    final public int getBlockData(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) throws ChunkException {
        if (data != 0) throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) throws ChunkException {
        if (level != 0) throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 15;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) throws ChunkException {
        if (level != 15) throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public byte[] getBytes() {
        return new byte[6144];
    }

    @Override
    public ChunkSection clone() {
        return this;
    }
}
