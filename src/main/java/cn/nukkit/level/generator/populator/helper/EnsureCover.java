package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.SNOW_LAYER;

/**
 * @author DaPorkchop_
 */
public interface EnsureCover {
    static boolean ensureCover(int x, int y, int z, IChunk chunk) {
        Identifier id = chunk.getBlockId(x, y, z);
        return id == AIR || id == SNOW_LAYER;
    }
}
