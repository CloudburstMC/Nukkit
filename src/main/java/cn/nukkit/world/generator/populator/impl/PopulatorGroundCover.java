package cn.nukkit.world.generator.populator.impl;

import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.ChunkManager;
import cn.nukkit.world.biome.Biome;
import cn.nukkit.world.biome.EnumBiome;
import cn.nukkit.world.biome.type.CoveredBiome;
import cn.nukkit.world.format.FullChunk;
import cn.nukkit.world.generator.populator.type.Populator;

/**
 * @author DaPorkchop_
 */
public class PopulatorGroundCover extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        //reverse iteration to 0 is faster
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
                Biome realBiome = EnumBiome.getBiome(chunk.getBiomeId(x, z));
                if (realBiome instanceof CoveredBiome) {
                    ((CoveredBiome) realBiome).doCover(x, z, chunk);
                }
            }
        }
    }
}
