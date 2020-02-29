package cn.nukkit.level.generator;

import cn.nukkit.level.chunk.IChunk;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public class PopChunkManager extends SimpleChunkManager {
    private boolean clean = true;
    private final IChunk[] chunks = new IChunk[9];
    private int CX = 0;
    private int CZ = 0;

    public PopChunkManager() {
        super();
    }

    @Override
    public void clean() {
        super.clean();
        if (!this.clean) {
            Arrays.fill(chunks, null);
            CX = 0;
            CZ = 0;
            this.clean = true;
        }
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ) {
        int offsetX = (chunkX - CX) + 1;
        int offsetZ = (chunkZ - CZ) + 1;
        checkArgument(offsetX >= 0 && offsetX < 3 && offsetZ >= 0 && offsetZ < 3,
                "Chunk (%s, %s) is outside population area (%s, %s)", chunkX, chunkZ, CX, CZ);

        return chunks[offsetX + (offsetZ * 3)];
    }

    @Override
    public void setChunk(IChunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        if (clean) {
            CX = chunkX + 1; //this method is called with sorted chunks, meaning the first chunk is the one with the lowest position
            CZ = chunkZ + 1;
            clean = false;
        }

        int offsetX = (chunkX - CX) + 1;
        int offsetZ = (chunkZ - CZ) + 1;
        checkArgument(offsetX >= 0 && offsetX < 3 && offsetZ >= 0 && offsetZ < 3,
                "Chunk (%s, %s) is outside population area (%s, %s)", chunkX, chunkZ, CX, CZ);

        this.chunks[offsetX + (offsetZ * 3)] = chunk;
    }
}
