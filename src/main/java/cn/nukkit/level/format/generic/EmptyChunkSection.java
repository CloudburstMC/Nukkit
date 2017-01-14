package cn.nukkit.level.format.generic;

import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.utils.ChunkException;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EmptyChunkSection implements ChunkSection {
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
    public byte[] getBlockIdColumn(int x, int z) {
        return new byte[16];
    }

    @Override
    public byte[] getBlockDataColumn(int x, int z) {
        return new byte[8];
    }

    @Override
    public byte[] getBlockSkyLightColumn(int x, int z) {
        return new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    }

    @Override
    public byte[] getBlockLightColumn(int x, int z) {
        return new byte[8];
    }

    @Override
    public int getFullBlock(int x, int y, int z) throws ChunkException {
        return 0;
    }

    @Override
    public boolean setBlock(int x, int y, int z) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public boolean setBlock(int x, int y, int z, Integer blockId) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public boolean setBlock(int x, int y, int z, Integer blockId, Integer meta) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
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
        byte[] b = new byte[2048];
        Arrays.fill(b, (byte) 0xff);
        return b;
    }

    @Override
    public byte[] getLightArray() {
        return new byte[2048];
    }

    @Override
    final public void setBlockId(int x, int y, int z, int id) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    final public int getBlockData(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 15;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(10240);
        byte[] skyLight = new byte[2048];
        Arrays.fill(skyLight, (byte) 0xff);
        buffer.position(6144);
        return buffer
                .put(skyLight)
                .array();
    }

    @Override
    public ChunkSection clone() {
        return this;
    }
}
