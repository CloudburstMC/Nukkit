package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public interface EnsureGrassBelow {

    static boolean ensureGrassBelow(int x, int y, int z, FullChunk chunk)  {
        int bid = chunk.getBlockId(x, y - 1, z);
        return bid == BlockID.GRASS || bid == BlockID.PODZOL;
        //return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }
}
