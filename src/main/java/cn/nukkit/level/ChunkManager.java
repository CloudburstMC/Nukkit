package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.format.generic.BaseFullChunk;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public interface ChunkManager {

    default int getBlockIdAt(int x, int y, int z) {
        return this.getBlockIdAt(x, y, z, Block.LAYER_NORMAL);
    }

    int getBlockIdAt(int x, int y, int z, BlockLayer layer);

    default void setBlockFullIdAt(int x, int y, int z, int fullId) {
        this.setBlockFullIdAt(x, y, z, Block.LAYER_NORMAL, fullId);
    }

    void setBlockFullIdAt(int x, int y, int z, BlockLayer layer, int fullId);

    default void setBlockIdAt(int x, int y, int z, int id) {
        this.setBlockIdAt(x, y, z, Block.LAYER_NORMAL, id);
    }

    void setBlockIdAt(int x, int y, int z, BlockLayer layer, int id);

    default void setBlockAt(int x, int y, int z, int id) {
        setBlockAt(x, y, z, id, 0);
    }

    void setBlockAt(int x, int y, int z, int id, int data);

    boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id);
    boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id, int data);

    default int getBlockDataAt(int x, int y, int z) {
        return this.getBlockDataAt(x, y, z, Block.LAYER_NORMAL);
    }

    int getBlockDataAt(int x, int y, int z, BlockLayer layer);

    default void setBlockDataAt(int x, int y, int z, int data) {
        this.setBlockDataAt(x, y, z, Block.LAYER_NORMAL, data);
    }

    void setBlockDataAt(int x, int y, int z, BlockLayer layer, int data);

    BaseFullChunk getChunk(int chunkX, int chunkZ);

    void setChunk(int chunkX, int chunkZ);

    void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk);

    long getSeed();

    default int getMinBlockY() {
        return 0;
    }

    default int getMaxBlockY() {
        return 255;
    }
}