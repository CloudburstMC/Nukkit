package cn.nukkit.level.generator.populator.helper;

import static cn.nukkit.block.BlockID.GRASS;
import cn.nukkit.level.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public interface EnsureGrassBelow {

    static boolean ensureGrassBelow(final int x, final int y, final int z, final FullChunk chunk) {
        return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }

}
