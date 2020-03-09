package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector2i;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.math.vector.Vector4i;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkManager {

    default Identifier getBlockId(Vector3i pos) {
        return this.getBlockId(pos.getX(), pos.getY(), pos.getZ());
    }

    default Identifier getBlockId(Vector4i pos) {
        return this.getBlockId(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    default Identifier getBlockId(int x, int y, int z) {
        return this.getBlockId(x, y, z, 0);
    }

    Identifier getBlockId(int x, int y, int z, int layer);

    default void setBlock(Vector3i pos, Identifier id) {
        this.setBlockId(pos.getX(), pos.getY(), pos.getZ(), id);
    }

    default void setBlockId(int x, int y, int z, Identifier id) {
        this.setBlockId(x, y, z, 0, id);
    }

    default void setBlockId(Vector3i pos, Identifier identifier) {
        this.setBlockId(pos.getX(), pos.getY(), pos.getZ(), 0, identifier);
    }

    default void setBlockId(Vector3i pos, int layer, Identifier identifier) {
        this.setBlockId(pos.getX(), pos.getY(), pos.getZ(), layer, identifier);
    }

    void setBlockId(int x, int y, int z, int layer, Identifier id);

    default int getBlockRuntimeIdUnsafe(int x, int y, int z) {
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

    default void setBlockAt(Vector3i pos, Identifier id, int data) {
        this.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), id, data);
    }

    default void setBlockAt(int x, int y, int z, Identifier id, int data) {
        this.setBlockAt(x, y, z, 0, id, data);
    }

    default void setBlockAt(int x, int y, int z, int layer, Identifier id, int data) {
        this.setBlockAt(x, y, z, layer, BlockRegistry.get().getBlock(id, data));
    }

    default void setBlockAt(Vector3i pos, Block block) {
        this.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), block);
    }

    default void setBlockAt(int x, int y, int z, Block block) {
        this.setBlockAt(x, y, z, 0, block);
    }

    void setBlockAt(int x, int y, int z, int layer, Block block);

    default IChunk getChunk(Vector3i pos) {
        return getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    default IChunk getChunk(Vector2i chunkPos) {
        return getChunk(chunkPos.getX(), chunkPos.getY());
    }

    IChunk getChunk(int chunkX, int chunkZ);

    long getSeed();
}
