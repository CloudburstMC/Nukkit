package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class PopulatorCrimsonForestGround extends Populator {

    private static IntArrayList getHighestWorkableBlocks(ChunkManager level, int x, int z) {
        int y;
        IntArrayList blockYs = new IntArrayList();
        for (y = 128; y > 0; --y) {
            int b = level.getBlockIdAt(x, y, z);
            if ((b == Block.CRIMSON_NYLIUM) && level.getBlockIdAt(x, y + 1, z) == 0) {
                blockYs.add(y + 1);
            }
        }
        return blockYs;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int amount = random.nextBoundedInt(64) + 32;

        for (int i = 0; i < amount; ++i) {
            int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
            int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
            IntArrayList ys = getHighestWorkableBlocks(level, x, z);
            for (int y : ys) {
                if (y <= 1) continue;

                int blockID;
                if (random.nextBoundedInt(6) == 0) {
                    if (random.nextBoundedInt(8) == 1) {
                        blockID = WARPED_FUNGUS;
                    } else {
                        blockID = CRIMSON_FUNGUS;
                    }
                } else {
                    blockID = CRIMSON_ROOTS;
                }

                level.setBlockAt(x, y, z, blockID);
            }
        }
    }
}
