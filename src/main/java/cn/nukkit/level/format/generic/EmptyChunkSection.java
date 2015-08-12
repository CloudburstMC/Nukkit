package cn.nukkit.level.format.generic;

import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.utils.ChunkException;

import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EmptyChunkSection implements ChunkSection {
    private int y;

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
    public int getFullBlock(int x, int y, int z) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public boolean setBlock(int x, int y, int z) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) throws ChunkException {
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
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) throws ChunkException {
        throw new ChunkException("Tried to modify an empty Chunk");
    }

    @Override
    public byte[] getBlockIdColumn(int x, int z) {
        byte[] b = new byte[16];
        Arrays.fill(b, (byte) 0x00);
        return b;
    }

    @Override
    public byte[] getBlockDataColumn(int x, int z) {
        byte[] b = new byte[8];
        Arrays.fill(b, (byte) 0x00);
        return b;
    }

    @Override
    public byte[] getBlockSkyLightColumn(int x, int z) {
        byte[] b = new byte[8];
        Arrays.fill(b, (byte) 0xff);
        return b;
    }

    @Override
    public byte[] getBlockLightColumn(int x, int z) {
        byte[] b = new byte[8];
        Arrays.fill(b, (byte) 0x00);
        return b;
    }

    @Override
    public byte[] getIdArray() {
        byte[] b = new byte[4096];
        Arrays.fill(b, (byte) 0x00);
        return b;
    }

    @Override
    public byte[] getDataArray() {
        byte[] b = new byte[2048];
        Arrays.fill(b, (byte) 0x00);
        return b;
    }

    @Override
    public byte[] getSkyLightArray() {
        byte[] b = new byte[2048];
        Arrays.fill(b, (byte) 0xff);
        return b;
    }

    @Override
    public byte[] getLightArray() {
        byte[] b = new byte[2048];
        Arrays.fill(b, (byte) 0x00);
        return b;
    }

}
