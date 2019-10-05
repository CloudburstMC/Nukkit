package cn.nukkit.level.chunk;

@FunctionalInterface
public interface ChunkDataLoader {

    /**
     * Loads data into specified chunk.
     *
     * @param chunk chunk
     * @return chunk dirty
     */
    boolean load(Chunk chunk);
}
