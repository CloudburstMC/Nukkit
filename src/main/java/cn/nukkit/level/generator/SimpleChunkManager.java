package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SimpleChunkManager implements ChunkManager {

    protected long seed;

    public SimpleChunkManager(final long seed) {
        this.seed = seed;
    }

    @Override
    public int getBlockIdAt(final int x, final int y, final int z) {
        final FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockId(x & 0xf, y & 0xff, z & 0xf);
        }
        return 0;
    }

    @Override
    public void setBlockFullIdAt(final int x, final int y, final int z, final int fullId) {
        final FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setFullBlockId(x & 0xf, y & 0xff, z & 0xf, fullId);
        }
    }

    @Override
    public void setBlockIdAt(final int x, final int y, final int z, final int id) {
        final FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockId(x & 0xf, y & 0xff, z & 0xf, id);
        }
    }

    @Override
    public void setBlockAt(final int x, final int y, final int z, final int id, final int data) {
        final FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlock(x & 0xf, y & 0xff, z & 0xf, id, data);
        }
    }

    @Override
    public int getBlockDataAt(final int x, final int y, final int z) {
        final FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockData(x & 0xf, y & 0xff, z & 0xf);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(final int x, final int y, final int z, final int data) {
        final FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockData(x & 0xf, y & 0xff, z & 0xf, data);
        }
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    public void setSeed(final long seed) {
        this.seed = seed;
    }

    public void cleanChunks(final long seed) {
        this.seed = seed;
    }

}
