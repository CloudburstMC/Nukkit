package cn.nukkit.level.generator;

import cn.nukkit.level.format.generic.BaseFullChunk;

public class SingleChunkManager extends SimpleChunkManager {
    private int CX = Integer.MAX_VALUE;
    private int CZ = Integer.MAX_VALUE;
    private BaseFullChunk chunk;

    public SingleChunkManager(long seed) {
        super(seed);
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        if (chunkX == CX && chunkZ == CZ) {
            return chunk;
        }
        return null;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk) {
        if (chunk == null) {
            this.chunk = null;
            this.CX = Integer.MAX_VALUE;
            this.CZ = Integer.MAX_VALUE;
        } else if (this.chunk != null) {
            throw new UnsupportedOperationException("Replacing chunks is not allowed behavior");
        } else {
            this.chunk = chunk;
            this.CX = chunk.getX();
            this.CZ = chunk.getZ();
        }
    }

    @Override
    public void cleanChunks(long seed) {
        super.cleanChunks(seed);
        chunk = null;
        CX = Integer.MAX_VALUE;
        CZ = Integer.MAX_VALUE;
    }
}
