package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SimpleChunkManager implements ChunkManager {

    protected long seed;

    public SimpleChunkManager(long seed) {
        this.seed = seed;
    }

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        return getBlockIdAt(x, y, z, 0);
    }

    @Override
    public int getBlockIdAt(int x, int y, int z, int layer) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockId(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return 0;
    }


    @Override
    public void setBlockIdAt(int x, int y, int z, int id) {
        setBlockIdAt(x, y, z, 0, id);
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int layer, int id) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockId(x & 0xf, y & 0xff, z & 0xf, layer, id);
        }
    }


    @Override
    public void setBlockAt(int x, int y, int z, int id, int data) {
        setBlockAtLayer(x, y, z, 0, id, data);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int id, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.setBlock(x & 0xf, y & 0xff, z & 0xf, id, data);
        }
        return false;
    }

    @Override
    public void setBlockFullIdAt(int x, int y, int z, int fullId) {
        setBlockFullIdAt(x, y, z, 0, fullId);
    }

    @Override
    public void setBlockFullIdAt(int x, int y, int z, int layer, int fullId) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setFullBlockId(x & 0xf, y & 0xff, z & 0xf, layer, fullId);
        }
    }


    @Override
    public int getBlockDataAt(int x, int y, int z) {
        return getBlockDataAt(x, y, z, 0);
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, int layer) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockData(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return 0;
    }


    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        setBlockDataAt(x, y, z, data, 0);
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int layer, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockData(x & 0xf, y & 0xff, z & 0xf, layer, data);
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void cleanChunks(long seed) {
        this.seed = seed;
    }
}
