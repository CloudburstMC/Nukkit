package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Niall Lindsay (Niall7459, Nukkit Project)
 */

public class PopulatorTallSugarcane extends PopulatorSugarcane {
    @Override
    protected void placeBlock(int x, int y, int z, int id, FullChunk chunk, NukkitRandom random) {
        int height = ThreadLocalRandom.current().nextInt(3) + 1;
        for (int i = 0; i < height; i++)    {
            chunk.setFullBlockId(x, y + i, z, id);
        }
    }
}
