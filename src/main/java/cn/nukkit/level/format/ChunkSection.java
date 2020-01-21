package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkSection {
    int getY();

    int getBlockId(int x, int y, int z);

    int getBlockId(int x, int y, int z, int layer);

    void setBlockId(int x, int y, int z, int id);

    boolean setFullBlockId(int x, int y, int z, int layer, int fullId);

    int getBlockData(int x, int y, int z);

    int getBlockData(int x, int y, int z, int layer);

    void setBlockData(int x, int y, int z, int data);

    void setBlockData(int x, int y, int z, int layer, int data);

    int getFullBlock(int x, int y, int z);

    Block getAndSetBlock(int x, int y, int z, int layer, Block block);

    Block getAndSetBlock(int x, int y, int z, Block block);

    void setBlockId(int x, int y, int z, int layer, int id);

    boolean setFullBlockId(int x, int y, int z, int fullId);

    int getFullBlock(int x, int y, int z, int layer);

    boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId);

    boolean setBlock(int x, int y, int z, int blockId);

    boolean setBlock(int x, int y, int z, int blockId, int meta);

    boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    byte[] getIdExtraArray(int layer);

    byte[] getIdArray(int layer);

    byte[] getIdArray();

    byte[] getDataArray();

    byte[] getDataArray(int layer);
    
    default byte[] getDataExtraArray() {
        return getDataExtraArray(0);
    }
    
    byte[] getDataExtraArray(int layer);

    byte[] getSkyLightArray();

    byte[] getLightArray();

    boolean isEmpty();

    byte[] getBytes();
    
    int getMaximumLayer();

    CompoundTag toNBT();

    ChunkSection copy();
}
