package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

public class WaterIcePopulator extends Populator {

    @Override
    public void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final Biome biome = EnumBiome.getBiome(chunk.getBiomeId(x, z));
                if (biome.isFreezing()) {
                    final int topBlock = chunk.getHighestBlockAt(x, z);
                    if (chunk.getBlockId(x, topBlock, z) == BlockID.STILL_WATER) {
                        chunk.setBlockId(x, topBlock, z, BlockID.ICE);
                    }
                }
            }
        }
    }

}
