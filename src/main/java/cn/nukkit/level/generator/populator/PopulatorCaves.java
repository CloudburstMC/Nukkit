package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.WateryBiome;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.math.NukkitRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class PopulatorCaves extends Populator {

    private Simplex cavesSimplex = null;

    public void initPopulate(NukkitRandom random) {
        if (this.cavesSimplex != null) {
            return;
        }
        this.cavesSimplex = new Simplex(random, 4F, 1F / 2F, 1F / 30F);
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        initPopulate(random);
        FullChunk chunk = level.getChunk(chunkX, chunkZ);
        double[][][] cavesGenerate = Generator.getFastNoise3D(this.cavesSimplex, 16, 128, 16, 4, 4, 4, chunkX * 16, 0, chunkZ * 16);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                Biome biome = Biome.getBiome(chunk.getBiomeId(x, z));
                Boolean hasWater = false;
                Boolean heightestBlock = true;
                if (biome instanceof WateryBiome) {
                    hasWater = true;
                    heightestBlock = false;
                }
                for (int y = 127; y >= 25; y--) {
                    if (chunk.getBlockId(x, y, z) == Block.AIR) {
                        continue;
                    }
                    if (chunk.getBlockId(x, y, z) == Block.WATER || chunk.getBlockId(x, y, z) == Block.STILL_WATER) {
                        hasWater = true;
                        heightestBlock = false;
                        continue;
                    }
                    if (hasWater) {
                        y -= 5;
                        hasWater = false;
                        continue;
                    }
                    if (cavesGenerate[x][z][y] > 0.35F) {
                        chunk.setBlock(x, y, z, Block.AIR);
                    } else if (heightestBlock) {
                        if (chunk.getBlockId(x, y, z) == Block.DIRT) {
                            chunk.setBlock(x, y, z, Block.GRASS);
                        }
                        heightestBlock = false;
                    }
                }

            }
        }
    }

}
