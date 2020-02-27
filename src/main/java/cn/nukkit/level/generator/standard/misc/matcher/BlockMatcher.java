package cn.nukkit.level.generator.standard.misc.matcher;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;

/**
 * Checks if a pattern of blocks matches the blocks at the given location.
 *
 * @author DaPorkchop_
 */
public interface BlockMatcher {
    /**
     * Checks whether the given position in the given {@link IChunk} matches.
     */
    boolean canPlace(IChunk chunk, int x, int y, int z);

    /**
     * Checks whether the given position in the given {@link ChunkManager} matches.
     */
    boolean canPlace(ChunkManager level, int x, int y, int z);
}
