package cn.nukkit.level.format;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkSection {
    int getY();

    int getBlockId(int x, int y, int z);

    int getBlockId(int x, int y, int z, int layer);

    void setBlockId(int x, int y, int z, int id);

    void setBlockId(int x, int y, int z, int id, int layer);

    int getBlockData(int x, int y, int z);

    int getBlockData(int x, int y, int z, int layer);

    void setBlockData(int x, int y, int z, int data);

    void setBlockData(int x, int y, int z, int data, int layer);

    int getFullBlock(int x, int y, int z);

    int getFullBlock(int x, int y, int z, int layer);

    Block getAndSetBlock(int x, int y, int z, Block block);

    Block getAndSetBlock(int x, int y, int z, Block block, int layer);

    boolean setFullBlockId(int x, int y, int z, int fullId);

    boolean setFullBlockId(int x, int y, int z, int fullId, int layer);

    boolean setBlock(int x, int y, int z, int blockId);

    boolean setBlock(int x, int y, int z, int blockId, int meta);

    boolean setBlock(int x, int y, int z, int blockId, int meta, int layer);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    byte[] getIdArray();

    byte[] getIdArray(int layer);

    byte[] getDataArray();

    byte[] getDataArray(int layer);

    byte[] getSkyLightArray();

    byte[] getLightArray();

    boolean isEmpty();

    byte[] getBytes();

    ChunkSection copy();

    byte[] getMatrixArray();

    byte[] getMatrixArray(int layer);

    boolean hasLayer2();
}
