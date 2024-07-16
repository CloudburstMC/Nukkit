package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 *
 * Places bedrock on the bottom of the world
 */
public class PopulatorBedrock extends Populator {

    private final boolean nether;

    public PopulatorBedrock() {
        this(false);
    }

    public PopulatorBedrock(boolean nether) {
        this.nether = nether;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (int x = 0; x < 16; x++)    {
            for (int z = 0; z < 16; z++)    {
                chunk.setBlockId(x, 0, z, BEDROCK);

                for (int i = 1; i < 5; i++) {
                    if (random.nextBoundedInt(i) == 0)  { // decreasing amount
                        chunk.setBlockId(x, i, z, BEDROCK);
                    }
                }

                if (this.nether) {
                    chunk.setBlockId(x, 127, z, BEDROCK);

                    for (int i = 126; i > 122; i--) {
                        if (random.nextBoundedInt(127 - i) == 0)  { // decreasing amount
                            chunk.setBlockId(x, i, z, BEDROCK);
                        }
                    }
                }
            }
        }
    }
}
