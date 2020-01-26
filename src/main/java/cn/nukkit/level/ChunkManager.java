package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkManager {
    default Identifier getBlockIdAt(int x, int y, int z) {
        return this.getBlockIdAt(x, y, z, 0);
    }

    Identifier getBlockIdAt(int x, int y, int z, int layer);

    default void setBlockIdAt(int x, int y, int z, Identifier id) {
        this.setBlockIdAt(x, y, z, 0, id);
    }

    void setBlockIdAt(int x, int y, int z, int layer, Identifier id);

    default int getBlockRuntimeIdUnsafe(int x, int y, int z)    {
        return this.getBlockRuntimeIdUnsafe(x, y, z, 0);
    }

    int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer);

    default void setBlockRuntimeIdUnsafe(int x, int y, int z, int runtimeId) {
        this.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
    }

    void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId);

    default int getBlockDataAt(int x, int y, int z) {
        return this.getBlockDataAt(x, y, z, 0);
    }

    int getBlockDataAt(int x, int y, int z, int layer);

    default void setBlockDataAt(int x, int y, int z, int data) {
        this.setBlockDataAt(x, y, z, 0, data);
    }

    void setBlockDataAt(int x, int y, int z, int layer, int data);

    default Block getBlockAt(int x, int y, int z) {
        return this.getBlockAt(x, y, z, 0);
    }

    Block getBlockAt(int x, int y, int z, int layer);

    default void setBlockAt(int x, int y, int z, Identifier id, int data) {
        this.setBlockAt(x, y, z, 0, id, data);
    }

    default void setBlockAt(int x, int y, int z, int layer, Identifier id, int data) {
        this.setBlockAt(x, y, z, layer, BlockRegistry.get().getBlock(id, data));
    }

    default void setBlockAt(int x, int y, int z, Block block) {
        this.setBlockAt(x, y, z, 0, block);
    }

    void setBlockAt(int x, int y, int z, int layer, Block block);

    IChunk getChunk(int chunkX, int chunkZ);

    long getSeed();
}
