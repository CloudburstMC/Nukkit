package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.utils.Identifier;

/**
 * @author DaPorkchop_
 */
public interface EnsureBelow {
    static boolean ensureBelow(int x, int y, int z, Identifier id, IChunk chunk) {
        return chunk.getBlockId(x, y - 1, z) == id;
    }
}
