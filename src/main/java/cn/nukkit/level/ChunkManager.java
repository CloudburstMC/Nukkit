package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkManager {

    default Identifier getBlockIdAt(int x, int y, int z) {
        return getBlockIdAt(x, y, z, 0);
    }

    Identifier getBlockIdAt(int x, int y, int z, int layer);

    default void setBlockIdAt(int x, int y, int z, Identifier id) {
        setBlockIdAt(x, y, z, 0, id);
    }

    void setBlockIdAt(int x, int y, int z, int layer, Identifier id);

    default int getBlockDataAt(int x, int y, int z) {
        return getBlockDataAt(x, y, z, 0);
    }

    int getBlockDataAt(int x, int y, int z, int layer);

    default void setBlockDataAt(int x, int y, int z, int data) {
        setBlockDataAt(x, y, z, 0, data);
    }

    void setBlockDataAt(int x, int y, int z, int layer, int data);

    default Block getBlockAt(int x, int y, int z) {
        return getBlockAt(x, y, z, 0);
    }

    Block getBlockAt(int x, int y, int z, int layer);

    default void setBlockAt(int x, int y, int z, Identifier id, int data) {
        setBlockAt(x, y, z, 0, id, data);
    }

    default void setBlockAt(int x, int y, int z, int layer, Identifier id, int data) {
        setBlockAt(x, y, z, layer, BlockRegistry.get().getBlock(id, data));
    }

    default void setBlockAt(int x, int y, int z, Block block) {
        setBlockAt(x, y, z, 0, block);
    }

    void setBlockAt(int x, int y, int z, int layer, Block block);


    Chunk getChunk(int chunkX, int chunkZ);

    long getSeed();
}
