package cn.nukkit.level.generator;

import cn.nukkit.level.chunk.Chunk;

import java.util.Arrays;

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
        if (!clean) {
            Arrays.fill(chunks, null);
            CX = Integer.MAX_VALUE;
            CZ = Integer.MAX_VALUE;
            clean = true;
        }
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        int index;
        switch (chunkX - CX) {
            case -1:
                index = 0;
                break;
            case 0:
                index = 1;
                break;
            case 1:
                index = 2;
                break;
            default:
                return null;
        }
        switch (chunkZ - CZ) {
            case -1:
                break;
            case 0:
                index += 3;
                break;
            case 1:
                index += 6;
                break;
            default:
                return null;
        }
        return chunks[index];
    }

    @Override
    public void setChunk(Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        if (CX == Integer.MAX_VALUE) {
            CX = chunkX;
            CZ = chunkZ;
        }

        int index;
        switch (chunkX - CX) {
            case -1:
                index = 0;
                break;
            case 0:
                index = 1;
                break;
            case 1:
                index = 2;
                break;
            default:
                throw new IllegalArgumentException("Chunk (" + chunkX + ", " + chunkZ + ") is outside population area (" + CX + ", " + CZ + ")");
        }
        switch (chunkZ - CZ) {
            case -1:
                break;
            case 0:
                index += 3;
                break;
            case 1:
                index += 6;
                break;
            default:
                throw new IllegalArgumentException("Chunk (" + chunkX + ", " + chunkZ + ") is outside population area (" + CX + ", " + CZ + ")");
        }
        clean = false;
        chunks[index] = chunk;
    }
}
