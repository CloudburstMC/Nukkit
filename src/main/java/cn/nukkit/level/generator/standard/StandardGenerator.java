package cn.nukkit.level.generator.standard;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.biome.map.CachingBiomeMap;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.level.generator.standard.gen.Decorator;
import cn.nukkit.level.generator.standard.gen.DensitySource;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.registry.StandardGeneratorRegistries;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Strings;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.ThreadCache;
import net.daporkchop.lib.random.PRandom;

import static cn.nukkit.level.generator.standard.StandardGeneratorUtils.*;

/**
 * Main class of the NukkitX Standard Generator.
 *
 * @author DaPorkchop_
 */
public final class StandardGenerator implements Generator {
    public static final Identifier ID = Identifier.fromString("minecraft:standard");

    private static final String DEFAULT_PRESET = "nukkitx:overworld";

    private static final int    STEP          = 4;
    private static final double D_STEP        = 1.0d / (double) STEP;
    private static final int    CACHE_WIDTH   = 16 / STEP + 1;
    private static final int    CACHE_HEIGHT  = 256 / STEP + 1;
    private static final int    ICACHE_WIDTH  = 16 + 1;
    private static final int    ICACHE_HEIGHT = 256 + 1;

    private static final Cache<ThreadData> THREAD_DATA_CACHE = ThreadCache.soft(ThreadData::new);

    private final BiomeMap        biomes;
    private final DensitySource   density;
    private final BlockReplacer[] replacers;
    private final Decorator[] decorators;
    private final Populator[]     populators;

    public StandardGenerator(long seed, String options) {
        Identifier presetId = Identifier.fromString(Strings.isNullOrEmpty(options) ? DEFAULT_PRESET : options);
        Config preset = StandardGeneratorUtils.loadUnchecked("preset", presetId);

        ConfigSection biomesConfig = preset.getSection("generation.biomes");
        this.biomes = StandardGeneratorRegistries.biomeMap()
                .apply(biomesConfig, computeRandom(seed, "generation.biomes", biomesConfig));

        ConfigSection densityConfig = preset.getSection("generation.density");
        this.density = StandardGeneratorRegistries.densitySource()
                .apply(densityConfig, computeRandom(seed, "generation.density", densityConfig));

        this.replacers = preset.<ConfigSection>getList("generation.replacers").stream()
                .map(section -> StandardGeneratorRegistries.blockReplacer()
                        .apply(section, computeRandom(seed, "generation.replacers", section)))
                .toArray(BlockReplacer[]::new);

        this.decorators = preset.<ConfigSection>getList("generation.decorators").stream()
                .map(section -> StandardGeneratorRegistries.decorator()
                        .apply(section, computeRandom(seed, "generation.decorators", section)))
                .toArray(Decorator[]::new);

        this.populators = preset.<ConfigSection>getList("population.populators").stream()
                .map(section -> StandardGeneratorRegistries.populator()
                        .apply(section, computeRandom(seed, "population.populators", section)))
                .toArray(Populator[]::new);
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
        final int baseX = chunkX << 4;
        final int baseZ = chunkZ << 4;
        final ThreadData threadData = THREAD_DATA_CACHE.get();

        final CachingBiomeMap biomeMap = threadData.biomeMap.init(this.biomes);

        //compute initial densities
        final double[] densityCache = threadData.densityCache;
        for (int i = 0, x = 0; x < CACHE_WIDTH; x++) {
            for (int y = 0; y < CACHE_HEIGHT; y++) {
                for (int z = 0; z < CACHE_WIDTH; z++) {
                    densityCache[i++] = this.density.get(baseX + x * STEP, y * STEP, baseZ + z * STEP, biomeMap);
                }
            }
        }

        //interpolate densities
        final double[] iDensityCache = threadData.iDensityCache;
        for (int sectionX = 0; sectionX < CACHE_WIDTH - 1; sectionX++) {
            for (int sectionY = 0; sectionY < CACHE_HEIGHT - 1; sectionY++) {
                for (int i = (sectionX * CACHE_HEIGHT + sectionY) * CACHE_WIDTH, sectionZ = 0; sectionZ < CACHE_WIDTH - 1; sectionZ++, i++) {
                    double dxyz = densityCache[i];
                    double dxyZ = densityCache[i + 1];
                    double dxYz = densityCache[i + CACHE_WIDTH];
                    double dxYZ = densityCache[i + CACHE_WIDTH + 1];
                    double dXyz = densityCache[i + CACHE_HEIGHT * CACHE_WIDTH];
                    double dXyZ = densityCache[i + CACHE_HEIGHT * CACHE_WIDTH + 1];
                    double dXYz = densityCache[i + CACHE_HEIGHT * CACHE_WIDTH + CACHE_WIDTH];
                    double dXYZ = densityCache[i + CACHE_HEIGHT * CACHE_WIDTH + CACHE_WIDTH + 1];

                    double bx00 = dxyz;
                    double bx01 = dxyZ;
                    double bx10 = dxYz;
                    double bx11 = dxYZ;
                    double gx00 = (dXyz - dxyz) * D_STEP;
                    double gx01 = (dXyZ - dxyZ) * D_STEP;
                    double gx10 = (dXYz - dxYz) * D_STEP;
                    double gx11 = (dXYZ - dxYZ) * D_STEP;

                    for (int stepX = 0; stepX < STEP; stepX++) {
                        double ix00 = bx00 + gx00 * stepX;
                        double ix01 = bx01 + gx01 * stepX;
                        double ix10 = bx10 + gx10 * stepX;
                        double ix11 = bx11 + gx11 * stepX;

                        double by0 = ix00;
                        double by1 = ix01;
                        double gy0 = (ix10 - ix00) * D_STEP;
                        double gy1 = (ix11 - ix01) * D_STEP;

                        for (int stepY = 0; stepY < STEP; stepY++) {
                            double iy0 = by0 + gy0 * stepY;
                            double iy1 = by1 + gy1 * stepY;

                            double bz = iy0;
                            double gz = (iy1 - iy0) * D_STEP;

                            for (int stepZ = 0; stepZ < STEP; stepZ++) {
                                double iz = bz + gz * stepZ;

                                int blockX = sectionX * STEP | stepX;
                                int blockY = sectionY * STEP | stepY;
                                int blockZ = sectionZ * STEP | stepZ;

                                iDensityCache[(blockX * ICACHE_HEIGHT + blockY) * ICACHE_WIDTH + blockZ] = iz;
                            }
                        }
                    }
                }
            }
        }

        //run replacers
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int i = (x * ICACHE_HEIGHT + y) * ICACHE_WIDTH, z = 0; z < 16; z++, i++) {
                    double d = iDensityCache[i];
                    double gradX = iDensityCache[i + ICACHE_HEIGHT * ICACHE_WIDTH] - d;
                    double gradY = iDensityCache[i + ICACHE_WIDTH] - d;
                    double gradZ = iDensityCache[i + 1] - d;

                    Block block = null;
                    for (BlockReplacer replacer : this.replacers) {
                        block = replacer.replace(block, x | baseX, y, z | baseZ, gradX, gradY, gradZ, d);
                    }
                    if (block != null) {
                        chunk.setBlock(x, y, z, 0, block);
                    }
                }
            }
        }

        //run decorators
        for (Decorator decorator : this.decorators) {
            decorator.decorate(chunk, random);
        }

        //set biomes
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBiome(x, z, biomeMap.get(x | baseX, z | baseZ).getRuntimeId());
            }
        }

        //clean up
        biomeMap.clear();
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        for (Populator populator : this.populators) {
            populator.populate(random, level, chunkX, chunkZ);
        }
    }

    private static final class ThreadData {
        private final double[]        densityCache  = new double[CACHE_WIDTH * CACHE_HEIGHT * CACHE_WIDTH];
        private final double[]        iDensityCache = new double[ICACHE_WIDTH * ICACHE_HEIGHT * ICACHE_WIDTH];
        private final CachingBiomeMap biomeMap      = new CachingBiomeMap();
    }
}
