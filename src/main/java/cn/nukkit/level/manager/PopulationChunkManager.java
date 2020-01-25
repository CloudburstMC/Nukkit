package cn.nukkit.level.manager;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.utils.Identifier;
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
    }

    @Override
    public Identifier getBlockIdAt(int x, int y, int z, int layer) {
        return null;
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int layer, Identifier id) {
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int layer, int data) {
    }

    @Override
    public Block getBlockAt(int x, int y, int z, int layer) {
        return null;
    }

    @Override
    public void setBlockAt(int x, int y, int z, int layer, Block block) {
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ) {
        IChunk chunk = this.readChunks.get(Chunk.key(chunkX, chunkZ));
        return chunk != null ? chunk : this.level.getChunk(chunkX, chunkZ);
    }

    @Override
    public long getSeed() {
        return this.level.getSeed();
    }
}
