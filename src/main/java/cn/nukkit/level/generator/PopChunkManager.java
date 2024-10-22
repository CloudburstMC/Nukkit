package cn.nukkit.level.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.format.generic.BaseFullChunk;
import java.util.Arrays;
import java.util.function.Supplier;

public class PopChunkManager extends SimpleChunkManager {

    private boolean clean = true;
    private final BaseFullChunk[] chunks = new BaseFullChunk[9];
    private int CX = Integer.MAX_VALUE;
    private int CZ = Integer.MAX_VALUE;

    private final Supplier<DimensionData> dimensionDataSupplier;

    public PopChunkManager(long seed) {
        this(seed, () -> DimensionData.LEGACY_DIMENSION);
    }

    public PopChunkManager(long seed, Supplier<DimensionData> dimensionDataSupplier) {
        super(seed);
        this.dimensionDataSupplier = dimensionDataSupplier;
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
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        int index;
        switch (chunkX - CX) {
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
        switch (chunkZ - CZ) {
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
        return chunks[index];
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk) {
        if (CX == Integer.MAX_VALUE) {
            CX = chunkX;
            CZ = chunkZ;
        }
        int index;
        switch (chunkX - CX) {
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
                throw new UnsupportedOperationException("Chunk is outside population area");
        }
        switch (chunkZ - CZ) {
            case 0:
                break;
            case 1:
                index += 3;
                break;
            case 2:
                index += 6;
                break;
            default:
                throw new UnsupportedOperationException("Chunk is outside population area");
        }
        clean = false;
        chunks[index] = chunk;
    }

    @Override
    public int getMaxBlockY() {
        return this.dimensionDataSupplier.get().getMaxHeight();
    }

    @Override
    public int getMinBlockY() {
        return this.dimensionDataSupplier.get().getMinHeight();
    }
}
