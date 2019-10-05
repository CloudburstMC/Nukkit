package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.chunk.Chunk;

import static cn.nukkit.block.BlockID.GRASS;

/**
 * @author DaPorkchop_
 */
public interface EnsureGrassBelow {
    static boolean ensureGrassBelow(int x, int y, int z, Chunk chunk) {
        return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }
}
