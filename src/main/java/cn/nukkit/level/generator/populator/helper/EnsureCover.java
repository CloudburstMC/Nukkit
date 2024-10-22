package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.format.FullChunk;

import static cn.nukkit.block.BlockID.*;

/**
 * @author DaPorkchop_
 */
public interface EnsureCover {

    static boolean ensureCover(int x, int y, int z, FullChunk chunk) {
        if (y > 255) return false;
        int id = chunk.getBlockId(x, y, z);
        return id == AIR || id == SNOW_LAYER;
    }

    static boolean ensureWaterCover(int x, int y, int z, FullChunk chunk) {
        if (y > 255) return false;
        return chunk.getBlockId(x, y, z) == STILL_WATER;
    }
}
