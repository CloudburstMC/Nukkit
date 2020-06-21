package cn.nukkit.level.generator.populator.helper;

import static cn.nukkit.block.BlockID.AIR;
import static cn.nukkit.block.BlockID.SNOW_LAYER;
import cn.nukkit.level.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public interface EnsureCover {

    static boolean ensureCover(final int x, final int y, final int z, final FullChunk chunk) {
        final int id = chunk.getBlockId(x, y, z);
        return id == AIR || id == SNOW_LAYER;
    }

}
