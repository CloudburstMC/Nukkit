package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.chunk.Chunk;

import static cn.nukkit.block.BlockID.AIR;
import static cn.nukkit.block.BlockID.SNOW_LAYER;

/**
 * @author DaPorkchop_
 */
public interface EnsureCover {
    static boolean ensureCover(int x, int y, int z, Chunk chunk) {
        int id = chunk.getBlockId(x, y, z);
        return id == AIR || id == SNOW_LAYER;
    }
}
