package cn.nukkit.world.generator.populator.helper;

import static cn.nukkit.block.BlockID.GRASS;

import cn.nukkit.world.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public interface EnsureGrassBelow {
    static boolean ensureGrassBelow(int x, int y, int z, FullChunk chunk)  {
        return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }
}
