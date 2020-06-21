package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public interface EnsureBelow {

    static boolean ensureBelow(final int x, final int y, final int z, final int id, final FullChunk chunk) {
        return chunk.getBlockId(x, y - 1, z) == id;
    }

}
