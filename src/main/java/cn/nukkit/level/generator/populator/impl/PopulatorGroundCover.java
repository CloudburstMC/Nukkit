package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorGroundCover extends Populator {
    public static final int STONE = BlockID.STONE << Block.DATA_BITS;

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
