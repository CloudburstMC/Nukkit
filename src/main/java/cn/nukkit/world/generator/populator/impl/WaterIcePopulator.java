package cn.nukkit.world.generator.populator.impl;

import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.ChunkManager;
import cn.nukkit.world.biome.Biome;
import cn.nukkit.world.biome.EnumBiome;
import cn.nukkit.world.format.FullChunk;
import cn.nukkit.world.generator.populator.type.Populator;

import static cn.nukkit.block.BlockID.ICE;
import static cn.nukkit.block.BlockID.STILL_WATER;

public class WaterIcePopulator extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = EnumBiome.getBiome(chunk.getBiomeId(x, z));
                if (biome.isFreezing()) {
                    int topBlock = chunk.getHighestBlockAt(x, z);
                    if (chunk.getBlockId(x, topBlock, z) == STILL_WATER)     {
                        chunk.setBlockId(x, topBlock, z, ICE);
                    }
                }
            }
        }
    }
}
