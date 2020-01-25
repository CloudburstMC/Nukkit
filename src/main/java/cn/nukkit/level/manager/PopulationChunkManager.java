package cn.nukkit.level.manager;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.List;

/**
 * Implementation of {@link ChunkManager} used during chunk population.
 *
 * @author DaPorkchop_
 */
public final class PopulationChunkManager implements ChunkManager {
    private final Long2ObjectMap<Chunk> readChunks  = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<Chunk> writeChunks = new Long2ObjectOpenHashMap<>();
    private final Level level;

    public PopulationChunkManager(Chunk chunk, List<Chunk> populationChunks) {
        this.level = chunk.getLevel();

        this.readChunks.put(Chunk.key(chunk.getX(), chunk.getZ()), chunk);
        for (Chunk populationChunk : populationChunks) {
            this.readChunks.put(Chunk.key(populationChunk.getX(), populationChunk.getZ()), populationChunk);
        }

        //porktodo: identify chunks that should be read-only based on whether they were loaded from disk
        /*this.writeChunks.put(Chunk.key(chunk.getX(), chunk.getZ()), chunk);
        for (Chunk populationChunk : populationChunks) {
            this.writeChunks.put(Chunk.key(populationChunk.getX(), populationChunk.getZ()), populationChunk);
        }*/
        this.writeChunks.putAll(this.readChunks);
    }

    @Override
    public Identifier getBlockIdAt(int x, int y, int z, int layer) {
        Chunk chunk = this.readChunks.get(Chunk.key(x >> 4, z >> 4));
        return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .getBlockId(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int layer, Identifier id) {
        Chunk chunk = this.writeChunks.get(Chunk.key(x >> 4, z >> 4));
        Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .setBlockId(x & 0xF, y, z & 0xF, layer, id);
    }

    @Override
    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
        Chunk chunk = this.readChunks.get(Chunk.key(x >> 4, z >> 4));
        return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .getBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
        Chunk chunk = this.writeChunks.get(Chunk.key(x >> 4, z >> 4));
        Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .setBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, layer, runtimeId);
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, int layer) {
        Chunk chunk = this.readChunks.get(Chunk.key(x >> 4, z >> 4));
        return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .getBlockData(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int layer, int data) {
        Chunk chunk = this.writeChunks.get(Chunk.key(x >> 4, z >> 4));
        Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .setBlockData(x & 0xF, y, z & 0xF, layer, data);
    }

    @Override
    public Block getBlockAt(int x, int y, int z, int layer) {
        Chunk chunk = this.readChunks.get(Chunk.key(x >> 4, z >> 4));
        return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .getBlock(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int layer, Block block) {
        Chunk chunk = this.writeChunks.get(Chunk.key(x >> 4, z >> 4));
        Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                .setBlock(x & 0xF, y, z & 0xF, layer, block);
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ) {
        return Preconditions.checkNotNull(this.readChunks.get(Chunk.key(chunkX, chunkZ)), "Chunk position (%s,%s) out of population bounds", chunkX, chunkZ);
    }

    @Override
    public long getSeed() {
        return this.level.getSeed();
    }
}
