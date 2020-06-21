package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 * <p>
 * Places bedrock on the bottom of the world
 */
public class PopulatorBedrock extends Populator {

    @Override
    public void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlockId(x, 0, z, BlockID.BEDROCK);
                for (int i = 1; i < 5; i++) {
                    if (random.nextBoundedInt(i) == 0) { //decreasing amount
                        chunk.setBlockId(x, i, z, BlockID.BEDROCK);
                    }
                }
            }
        }
    }

}
