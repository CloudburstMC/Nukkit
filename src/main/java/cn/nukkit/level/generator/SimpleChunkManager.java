package cn.nukkit.level.generator;

import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class SimpleChunkManager implements ChunkManager {

    protected long seed;

    public SimpleChunkManager(long seed) {
        this.seed = seed;
    }

    @Override
    public int getBlockIdAt(int x, int y, int z, BlockLayer layer) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockId(x & 0xf, y, z & 0xf, layer);
        }
        return 0;
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, BlockLayer layer, int id) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockId(x & 0xf, y, z & 0xf, layer, id);
        }
    }

    @Override
    public void setBlockAt(int x, int y, int z, int id, int data) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }

        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlock(x & 0xf, y, z & 0xf, id, data);
        }
    }

    @Override
    public void setBlockFullIdAt(int x, int y, int z, BlockLayer layer, int fullId) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setFullBlockId(x & 0xf, y, z & 0xf, layer, fullId);
        }
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, BlockLayer layer) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockData(x & 0xf, y, z & 0xf, layer);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, BlockLayer layer, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockData(x & 0xf, y, z & 0xf, layer, data);
        }
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id) {
        return this.setBlockAtLayer(x, y, z, layer, id, 0);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id, int data) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return false;
        }
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.setBlockAtLayer(x, y, z, layer, id, data);
        }
        return true;
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
