package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

public class WaterIcePopulator extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = EnumBiome.getBiome(chunk.getBiome(x, z));
                if (biome.isFreezing()) {
                    int topBlock = chunk.getHighestBlock(x, z);
                    if (chunk.getBlockId(x, topBlock, z) == STILL_WATER)     {
                        chunk.setBlockId(x, topBlock, z, ICE);
                    }
                }
            }
        }
    }
}
