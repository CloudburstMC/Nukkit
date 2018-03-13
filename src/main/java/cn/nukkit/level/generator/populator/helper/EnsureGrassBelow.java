package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.format.FullChunk;

import static cn.nukkit.block.BlockID.GRASS;

/**
 * @author DaPorkchop_
 */
public interface EnsureGrassBelow {
    static boolean ensureGrassBelow(int x, int y, int z, FullChunk chunk)  {
        return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }
}
