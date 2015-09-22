package cn.nukkit.level;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SimpleChunkManager implements ChunkManager {
    protected Map<String, FullChunk> chunks = new HashMap<>();

    protected int seed;

    public SimpleChunkManager(int seed) {
        this.seed = seed;
    }

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockId(x & 0xf, y & 0xf, z & 0xf);
        }
        return 0;
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int id) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockId(x & 0xf, y & 0xf, z & 0xf, id);
        }
    }

    @Override
    public int getBlockDataAt(int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockData(x & 0xf, y & 0xf, z & 0xf);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockData(x & 0xf, y & 0xf, z & 0xf, data);
        }
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        String index = Level.chunkHash(chunkX, chunkZ);
        return this.chunks.containsKey(index) ? (BaseFullChunk) this.chunks.get(index) : null;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (chunk == null) {
            this.chunks.remove(Level.chunkHash(chunkX, chunkZ));
            return;
        }
        this.chunks.put(Level.chunkHash(chunkX, chunkZ), (BaseFullChunk) chunk);
    }

    @Override
    public int getSeed() {
        return seed;
    }
}
