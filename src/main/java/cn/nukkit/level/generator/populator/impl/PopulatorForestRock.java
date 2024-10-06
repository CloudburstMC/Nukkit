package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

public class PopulatorForestRock extends PopulatorSurfaceBlock {

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return MOSSY_STONE << Block.DATA_BITS;
    }

    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int radiusX = random.nextBoundedInt(2);
        int radiusZ = random.nextBoundedInt(2);
        int radiusY = random.nextBoundedInt(2);
        float f = (radiusX + radiusZ + radiusY) * 0.333F + 0.5F;
        float fsquared = f * f;
        if (fsquared <= 0.7) return; // Skip one block sized ones

        int rx = random.nextBoundedInt(16);
        int rz = random.nextBoundedInt(16);
        int sourceX = (chunkX << 4) + rx;
        int sourceZ = (chunkZ << 4) + rz;
        int sourceY = getHighestWorkableBlock(level, rx, rz, chunk);

        boolean groundReached = false;
        while (sourceY > 3) {
            sourceY--;
            int block = level.getBlockIdAt(sourceX, sourceY, sourceZ);
            if (block == 0) {
                continue;
            }
            if (block == PODZOL || block == GRASS || block == DIRT || block == STONE) {
                groundReached = true;
                sourceY++;
                break;
            }
        }

        if (!groundReached || level.getBlockIdAt(sourceX, sourceY, sourceZ) != 0) {
            return;
        }

        for (int x = -radiusX; x <= radiusX; x++) {
            float xsquared = x * x;
            for (int z = -radiusZ; z <= radiusZ; z++) {
                float zsquared = z * z;
                for (int y = -radiusY; y <= radiusY; y++) {
                    if (xsquared + zsquared + y * y > fsquared) {
                        continue;
                    }
                    int bid = level.getBlockIdAt(sourceX + x, sourceY + y, sourceZ + z);
                    if (bid == AIR || bid == PODZOL || bid == GRASS || bid == DIRT || bid == STONE) {
                        level.setBlockFullIdAt(sourceX + x, sourceY + y, sourceZ + z, MOSSY_STONE << Block.DATA_BITS);
                    }
                }
            }
        }
    }
}
