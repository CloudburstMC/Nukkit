package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;

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
    public int getBlockIdAt(int x, int y, int z, int layer) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockId(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return 0;
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int layer, int id) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockId(x & 0xf, y & 0xff, z & 0xf, layer, id);
        }
    }

    @Override
    public Block getBlockAt(int x, int y, int z, int layer) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlock(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return Block.get(BlockID.AIR);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int layer, Block block) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlock(x & 0xf, y & 0xff, z & 0xf, layer, block);
        }
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, int layer) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockData(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int layer, int data) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockData(x & 0xf, y & 0xff, z & 0xf, layer, data);
        }
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

    public abstract void setChunk(Chunk chunk);
}
