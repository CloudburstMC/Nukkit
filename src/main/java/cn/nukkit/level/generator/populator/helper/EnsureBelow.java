package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public interface EnsureBelow {

    static boolean ensureBelow(int x, int y, int z, int id, FullChunk chunk) {
        return chunk.getBlockId(x, y - 1, z) == id;
    }
}
