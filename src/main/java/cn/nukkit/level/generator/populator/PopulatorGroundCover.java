package cn.nukkit.level.generator.populator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.CoveredBiome;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorGroundCover extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome realBiome = Biome.getBiome(chunk.getBiomeId(x, z));
                if (realBiome instanceof CoveredBiome) {
                    CoveredBiome biome = (CoveredBiome) realBiome;
                    int coverBlock = biome.getCoverBlock();
                    int surfaceDepth = biome.getSurfaceDepth();
                    int surfaceBlock = biome.getSurfaceBlock();
                    int surfaceMeta = biome.getSurfaceMeta();
                    int groundDepth = biome.getGroundDepth();
                    int groundBlock = biome.getGroundBlock();
                    int groundMeta = biome.getGroundMeta();
                    int stone = biome.getStoneBlock();

                    boolean hasCovered = false;
                    //start one below build limit in case of cover blocks
                    for (int y = 254; y > 0; y--)   {
                        if (chunk.getBlockId(x, y, z) == stone) {
                            if (!hasCovered) {
                                chunk.setBlockId(x, y + 1, z, coverBlock);
                                for (int i = 0; i < surfaceDepth; i++) {
                                    chunk.setBlockId(x, y - i, z, surfaceBlock);
                                    chunk.setBlockData(x, y - i, z, surfaceMeta);
                                }
                                y -= surfaceDepth;
                                for (int i = 0; i < groundDepth; i++) {
                                    chunk.setBlockId(x, y - i, z, groundBlock);
                                    chunk.setBlockData(x, y - i, z, groundMeta);
                                }
                                //don't take all of groundDepth away because we do y-- in the loop
                                y -= groundDepth - 1;
                                hasCovered = true;
                            }
                        } else {
                            if (hasCovered) {
                                //reset it if this isn't a valid stone block (allows us to place ground cover on top and below overhangs)
                                hasCovered = false;
                            }
                        }
                    }
                }
            }
        }
    }
}
