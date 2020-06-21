package cn.nukkit.level.generator;

import cn.nukkit.level.format.generic.BaseFullChunk;
import java.util.Arrays;

public class PopChunkManager extends SimpleChunkManager {

    private final BaseFullChunk[] chunks = new BaseFullChunk[9];

    private boolean clean = true;

    private int CX = Integer.MAX_VALUE;

    private int CZ = Integer.MAX_VALUE;

    public PopChunkManager(final long seed) {
        super(seed);
    }

    @Override
    public void cleanChunks(final long seed) {
        super.cleanChunks(seed);
        if (!this.clean) {
            Arrays.fill(this.chunks, null);
            this.CX = Integer.MAX_VALUE;
            this.CZ = Integer.MAX_VALUE;
            this.clean = true;
        }
    }

    @Override
    public BaseFullChunk getChunk(final int chunkX, final int chunkZ) {
        int index;
        switch (chunkX - this.CX) {
            case 0:
                index = 0;
                break;
            case 1:
                index = 1;
                break;
            case 2:
                index = 2;
                break;
            default:
                return null;
        }
        switch (chunkZ - this.CZ) {
            case 0:
                break;
            case 1:
                index += 3;
                break;
            case 2:
                index += 6;
                break;
            default:
                return null;
        }
        return this.chunks[index];
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ, final BaseFullChunk chunk) {
        if (this.CX == Integer.MAX_VALUE) {
            this.CX = chunkX;
            this.CZ = chunkZ;
        }
        int index;
        switch (chunkX - this.CX) {
            case 0:
                index = 0;
                break;
            case 1:
                index = 1;
                break;
            case 2:
                index = 2;
                break;
            default:
                throw new UnsupportedOperationException("WoolChunk is outside population area");
        }
        switch (chunkZ - this.CZ) {
            case 0:
                break;
            case 1:
                index += 3;
                break;
            case 2:
                index += 6;
                break;
            default:
                throw new UnsupportedOperationException("WoolChunk is outside population area");
        }
        this.clean = false;
        this.chunks[index] = chunk;
    }

}
