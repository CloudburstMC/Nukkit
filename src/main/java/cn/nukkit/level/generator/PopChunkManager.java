package cn.nukkit.level.generator;

import cn.nukkit.level.chunk.Chunk;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public class PopChunkManager extends SimpleChunkManager {
    private boolean clean = true;
    private final Chunk[] chunks = new Chunk[9];
    private int CX = Integer.MAX_VALUE;
    private int CZ = Integer.MAX_VALUE;

    public PopChunkManager(long seed) {
        super(seed);
    }

    @Override
    public void cleanChunks(long seed) {
        super.cleanChunks(seed);
        if (!this.clean) {
            Arrays.fill(chunks, null);
            CX = Integer.MAX_VALUE;
            CZ = Integer.MAX_VALUE;
            this.clean = true;
        }
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        int offsetX = (chunkX - CX) + 1;
        int offsetZ = (chunkZ - CZ) + 1;
        checkArgument(offsetX >= 0 && offsetX < 3 && offsetZ >= 0 && offsetZ < 3,
                "Chunk (%s, %s) is outside population area (%s, %s)", chunkX, chunkZ, CX, CZ);

        return chunks[offsetX + (offsetZ * 3)];
    }

    @Override
    public void setChunk(Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        if (CX == Integer.MAX_VALUE) {
            CX = chunkX;
            CZ = chunkZ;
        }

        int offsetX = (chunkX - CX) + 1;
        int offsetZ = (chunkZ - CZ) + 1;
        checkArgument(offsetX >= 0 && offsetX < 3 && offsetZ >= 0 && offsetZ < 3,
                "Chunk (%s, %s) is outside population area (%s, %s)", chunkX, chunkZ, CX, CZ);

        this.clean = false;
        this.chunks[offsetX + (offsetZ * 3)] = chunk;
    }
}
