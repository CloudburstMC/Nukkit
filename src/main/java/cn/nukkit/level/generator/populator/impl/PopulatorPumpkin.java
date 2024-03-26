package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.Utils;

public class PopulatorPumpkin extends Populator {

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (Utils.rand(0, 10) == 5) {
            int x = random.nextRange(0, 15);
            int z = random.nextRange(0, 15);
            int y = getHighestWorkableBlock(chunk, x, z);
            if (y != -1 && canPumpkinStay(chunk, x, y, z)) {
                chunk.setBlock(x, y, z, Block.PUMPKIN);
            }
        }
    }

    private static boolean canPumpkinStay(FullChunk chunk, int x, int y, int z) {
        int b = chunk.getBlockId(x, y, z);
        return (b == Block.AIR) && chunk.getBlockId(x, y - 1, z) == Block.GRASS;
    }

    private static int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        for (y = 0; y <= 127; ++y) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}