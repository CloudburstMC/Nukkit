package cn.nukkit.level.manager;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;

/**
 * Implementation of {@link ChunkManager} used during chunk population.
 *
 * @author DaPorkchop_
 */
public final class PopulationChunkManager implements ChunkManager {
    @Getter
    private final long seed;

    private final LockableChunk[] chunks = new LockableChunk[3 * 3];

    private final int cornerX;
    private final int cornerZ;

    public PopulationChunkManager(@NonNull Chunk chunk, @NonNull LockableChunk[] allChunks, long seed) {
        this.seed = seed;
        this.cornerX = chunk.getX() - 1;
        this.cornerZ = chunk.getZ() - 1;

        for (LockableChunk lockableChunk : allChunks)   {
            this.chunks[this.chunkIndex(lockableChunk.getX(), lockableChunk.getZ())] = lockableChunk;
        }
    }

    private int chunkIndex(int chunkX, int chunkZ)  {
        int relativeX = chunkX - this.cornerX;
        int relativeZ = chunkZ - this.cornerZ;
        Preconditions.checkArgument(relativeX >= 0 && relativeX < 3 && relativeZ >= 0 && relativeZ < 3, "Chunk position (%s,%s) out of population bounds", chunkX, chunkZ);
        return relativeX * 3 + relativeZ;
    }

    private LockableChunk chunkFromBlock(int blockX, int blockZ)  {
        int relativeX = (blockX >> 4) - this.cornerX;
        int relativeZ = (blockZ >> 4) - this.cornerZ;
        Preconditions.checkArgument(relativeX >= 0 && relativeX < 3 && relativeZ >= 0 && relativeZ < 3, "Block position (%s,%s) out of population bounds", blockX, blockZ);
        return this.chunks[relativeX * 3 + relativeZ];
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        return this.chunkFromBlock(x, z).getBlockId(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, Identifier id) {
        this.chunkFromBlock(x, z).setBlockId(x & 0xF, y, z & 0xF, layer, id);
    }

    @Override
    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
        return this.chunkFromBlock(x, z).getBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
        this.chunkFromBlock(x, z).setBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, layer, runtimeId);
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, int layer) {
        return this.chunkFromBlock(x, z).getBlockData(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int layer, int data) {
        this.chunkFromBlock(x, z).setBlockData(x & 0xF, y, z & 0xF, layer, data);
    }

    @Override
    public Block getBlockAt(int x, int y, int z, int layer) {
        return this.chunkFromBlock(x, z).getBlock(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int layer, Block block) {
        this.chunkFromBlock(x, z).setBlock(x & 0xF, y, z & 0xF, layer, block);
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ) {
        return this.chunks[this.chunkIndex(chunkX, chunkZ)];
    }
}
