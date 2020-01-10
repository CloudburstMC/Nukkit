package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.chunk.IChunk;

import static cn.nukkit.block.BlockIds.GRASS;

/**
 * @author DaPorkchop_
 */
public interface EnsureGrassBelow {
    static boolean ensureGrassBelow(int x, int y, int z, IChunk chunk) {
        return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }
}
