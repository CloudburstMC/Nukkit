package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitRandom;

import java.util.List;

public class PopulatorUnderwaterFloor extends PopulatorCount {

    private final double probability;
    private final int block;
    private final int radiusMin;
    private final int radiusMax;
    private final int radiusY;
    private final List<Integer> replaceBlocks;

    public PopulatorUnderwaterFloor(double probability, int block, int radiusMin, int radiusMax, int radiusY, List<Integer> replaceBlocks) {
        this.probability = probability;
        this.block = block;
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.radiusY = radiusY;
        this.replaceBlocks = replaceBlocks;
    }

    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextDouble() >= probability) {
            return;
        }

        int sourceX = (chunkX << 4) + random.nextBoundedInt(16);
        int sourceZ = (chunkZ << 4) + random.nextBoundedInt(16);
        int sourceY = getHighestWorkableBlock(level, sourceX, sourceZ, chunk) - 1;
        if (sourceY < radiusY) {
            return;
        }

        if (level.getBlockIdAt(sourceX, sourceY + 1, sourceZ) != BlockID.STILL_WATER) {
            return;
        }

        int radius = random.nextRange(radiusMin, radiusMax);
        for (int x = sourceX - radius; x <= sourceX + radius; x++) {
            for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= radius * radius) {
                    for (int y = sourceY - radiusY; y <= sourceY + radiusY; y++) {
                        for (int replaceBlockState : replaceBlocks) {
                            if (level.getBlockIdAt(x, y, z) == replaceBlockState) {
                                level.setBlockAt(x, y, z, block, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        int y;
        x &= 0xF;
        z &= 0xF;
        for (y = Normal.seaHeight - 1; y >= 0; --y) {
            if (!PopulatorHelpers.isNonOceanSolid(chunk.getBlockId(x, y, z))) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }
}
