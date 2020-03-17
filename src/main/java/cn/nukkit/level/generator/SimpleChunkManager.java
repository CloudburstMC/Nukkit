package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;
import static com.google.common.base.Preconditions.checkState;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SimpleChunkManager implements ChunkManager {

    protected Long seed;

    public SimpleChunkManager() {
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockId(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return AIR;
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, Identifier id) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockId(x & 0xf, y & 0xff, z & 0xf, layer, id);
        }
    }

    @Override
    public Block getBlockAt(int x, int y, int z, int layer) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlock(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return Block.get(AIR);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int layer, Block block) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlock(x & 0xf, y & 0xff, z & 0xf, layer, block);
        }
    }

    @Override
    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockRuntimeIdUnsafe(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return BlockRegistry.get().getRuntimeId(AIR, 0);
    }

    @Override
    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockRuntimeIdUnsafe(x & 0xF, y & 0xFF, z & 0xF, layer, runtimeId);
        }
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, int layer) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockData(x & 0xf, y & 0xff, z & 0xf, layer);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int layer, int data) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
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

    public void clean() {
        checkState(seed != null, "ChunkManager has already been cleaned");
        this.seed = null;
    }

    public abstract void setChunk(IChunk chunk);

    public void isValid(int chunkX, int chunkZ) {
        this.getChunk(chunkX, chunkZ);
    }
}
