package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.utils.BinaryStream;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public interface ChunkSection {

    int getY();

    default int getBlockId(int x, int y, int z) {
        return this.getBlockId(x, y, z, Block.LAYER_NORMAL);
    }

    int getBlockId(int x, int y, int z, BlockLayer layer);

    default void setBlockId(int x, int y, int z, int id) {
        this.setBlockId(x, y, z, Block.LAYER_NORMAL, id);
    }

    void setBlockId(int x, int y, int z, BlockLayer layer, int id);

    default int getBlockData(int x, int y, int z) {
        return this.getBlockData(x, y, z, Block.LAYER_NORMAL);
    }

    int getBlockData(int x, int y, int z, BlockLayer layer);

    default void setBlockData(int x, int y, int z, int data) {
        this.setBlockData(x, y, z, Block.LAYER_NORMAL, data);
    }

    void setBlockData(int x, int y, int z, BlockLayer layer, int data);

    default int getFullBlock(int x, int y, int z) {
        return this.getFullBlock(x, y, z, Block.LAYER_NORMAL);
    }

    int getFullBlock(int x, int y, int z, BlockLayer layer);

    default Block getAndSetBlock(int x, int y, int z, Block block) {
        return this.getAndSetBlock(x, y, z, Block.LAYER_NORMAL, block);
    }

    Block getAndSetBlock(int x, int y, int z, BlockLayer layer, Block block);

    default boolean setFullBlockId(int x, int y, int z, int fullId) {
        return this.setFullBlockId(x, y, z, Block.LAYER_NORMAL, fullId);
    }

    boolean setFullBlockId(int x, int y, int z, BlockLayer layer, int fullId);

    boolean setBlock(int x, int y, int z, int blockId);

    boolean setBlock(int x, int y, int z, int blockId, int meta);

    boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int blockId);

    boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int blockId, int meta);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    byte[] getIdArray();

    byte[] getDataArray();

    byte[] getSkyLightArray();

    byte[] getLightArray();

    boolean isEmpty();

    void writeTo(BinaryStream stream);

    ChunkSection copy();

    default ChunkSection copyForChunkSending() {
        return copy();
    }
}
