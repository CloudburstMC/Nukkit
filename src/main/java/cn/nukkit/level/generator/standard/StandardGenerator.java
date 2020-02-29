package cn.nukkit.level.generator.standard;

import cn.nukkit.Nukkit;
import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.GeneratorFactory;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.biome.map.CachingBiomeMap;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.density.DensitySource;
import cn.nukkit.level.generator.standard.gen.replacer.BlockReplacer;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.ThreadCache;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * Main class of the NukkitX Standard Generator.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
public final class StandardGenerator implements Generator {
    public static final Identifier ID = Identifier.fromString("minecraft:standard");

    private static final String DEFAULT_PRESET = "nukkitx:overworld";

    public static final GeneratorFactory FACTORY = (seed, options) -> {
        Identifier presetId = Identifier.fromString(Strings.isNullOrEmpty(options) ? DEFAULT_PRESET : options);
        try (InputStream in = StandardGeneratorUtils.read("preset", presetId)) {
            return Nukkit.YAML_MAPPER.readValue(in, StandardGenerator.class).init(seed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    private static final int    STEP          = 4;
    private static final double D_STEP        = 1.0d / (double) STEP;
    private static final int    CACHE_WIDTH   = 16 / STEP + 1;
    private static final int    CACHE_HEIGHT  = 256 / STEP + 1;
    private static final int    ICACHE_WIDTH  = 16 + 1;
    private static final int    ICACHE_HEIGHT = 256 + 1;

    private static final Cache<ThreadData> THREAD_DATA_CACHE = ThreadCache.soft(ThreadData::new);

    @JsonProperty
    private BiomeMap      biomes;
    @JsonProperty
    private DensitySource density;
    @JsonProperty
    private BlockReplacer[] replacers  = BlockReplacer.EMPTY_ARRAY;
    @JsonProperty
    private Decorator[]     decorators = Decorator.EMPTY_ARRAY;
    @JsonProperty
    private Populator[]     populators = Populator.EMPTY_ARRAY;

    private StandardGenerator init(long seed) {
        PRandom random = new FastPRandom(seed);
        Stream.of(this.biomes, this.density, this.replacers, this.decorators, this.populators)
                .flatMap(o -> o instanceof GenerationPass ? Stream.of((GenerationPass) o) : Stream.of((GenerationPass[]) o))
                .forEach(pass -> pass.init(seed, random.nextLong()));
        return this;
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
        final int baseX = chunkX << 4;
        final int baseZ = chunkZ << 4;
        final ThreadData threadData = THREAD_DATA_CACHE.get();

        final CachingBiomeMap biomeMap = threadData.biomeMap.init(this.biomes);

        //compute initial densities
        final double[] densityCache = threadData.densityCache;
        final DensitySource density = this.density; //avoid expensive getfield opcode
        for (int i = 0, x = 0; x < CACHE_WIDTH; x++) {
            for (int y = 0; y < CACHE_HEIGHT; y++) {
                for (int z = 0; z < CACHE_WIDTH; z++) {
                    densityCache[i++] = density.get(baseX + x * STEP, y * STEP, baseZ + z * STEP, biomeMap);
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
        final BlockReplacer[] replacers = this.replacers;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int i = (x * ICACHE_HEIGHT + y) * ICACHE_WIDTH, z = 0; z < 16; z++, i++) {
                    double d = iDensityCache[i];
                    double gradX = iDensityCache[i + ICACHE_HEIGHT * ICACHE_WIDTH] - d;
                    double gradY = iDensityCache[i + ICACHE_WIDTH] - d;
                    double gradZ = iDensityCache[i + 1] - d;

                    Block block = null;
                    for (BlockReplacer replacer : replacers) {
                        block = replacer.replace(block, x | baseX, y, z | baseZ, gradX, gradY, gradZ, d);
                    }
                    if (block != null) {
                        chunk.setBlock(x, y, z, 0, block);
                    }
                }
            }
        }

        //run decorators
        final Decorator[] decorators = this.decorators;
        for (Decorator decorator : decorators) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    decorator.decorate(chunk, random, x, z);
                }
            }
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
