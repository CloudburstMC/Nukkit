package cn.nukkit.level.generator.standard;

import cn.nukkit.Nukkit;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.GeneratorFactory;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.biome.map.CachingBiomeMap;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.density.DensitySource;
import cn.nukkit.level.generator.standard.misc.BiomeGenerationPass;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
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
            synchronized (StandardGenerator.class) {
                return Nukkit.YAML_MAPPER.readValue(in, StandardGenerator.class).init(seed);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    private static final int STEP_X = 4;
    private static final int STEP_Y = 8;
    private static final int STEP_Z = STEP_X;

    private static final int SAMPLES_X = 16 / STEP_X;
    private static final int SAMPLES_Y = 256 / STEP_Y;
    private static final int SAMPLES_Z = 16 / STEP_Z;

    private static final int CACHE_X = SAMPLES_X + 1;
    private static final int CACHE_Y = SAMPLES_Y + 1;
    private static final int CACHE_Z = SAMPLES_Z + 1;

    private static final double SCALE_X = 1.0d / STEP_X;
    private static final double SCALE_Y = 1.0d / STEP_Y;
    private static final double SCALE_Z = 1.0d / STEP_Z;

    private static final Ref<ThreadData> THREAD_DATA_CACHE = ThreadRef.soft(ThreadData::new);

    @JsonProperty
    private BiomeMap      biomes;
    @JsonProperty
    private DensitySource density;
    @JsonProperty
    private Decorator[] decorators = Decorator.EMPTY_ARRAY;
    @JsonProperty
    private Populator[] populators = Populator.EMPTY_ARRAY;

    private final Map<GenerationBiome, Decorator[]> decoratorLookup  = new ConcurrentHashMap<>();
    private final Map<GenerationBiome, Populator[]> populatorsLookup = new ConcurrentHashMap<>();

    private final Function<GenerationBiome, Decorator[]> decoratorLookupComputer  = biome -> Arrays.stream(this.decorators)
            .flatMap(decorator -> decorator instanceof BiomeGenerationPass ? Arrays.stream(biome.getDecorators()) : Stream.of(decorator))
            .toArray(Decorator[]::new);
    private final Function<GenerationBiome, Populator[]> populatorsLookupComputer = biome -> Arrays.stream(this.populators)
            .flatMap(populator -> populator instanceof BiomeGenerationPass ? Arrays.stream(biome.getPopulators()) : Stream.of(populator))
            .toArray(Populator[]::new);

    @JsonProperty("groundBlock")
    private int ground   = -1;
    @JsonProperty("seaBlock")
    private int sea      = -1;
    @JsonProperty
    private int seaLevel = -1;

    private StandardGenerator init(long seed) {
        //reset generation biome store to ensure that the replacers/decorators/populators for a given biome aren't initialized multiple times for multiple worlds
        Collection<GenerationBiome> biomes = StandardGeneratorStores.generationBiome().reset();

        Preconditions.checkState(this.ground >= 0, "groundBlock must be set!");
        Preconditions.checkState(this.seaLevel < 0 || this.sea >= 0, "seaBlock and seaLevel must either both be set or be omitted!");

        Collection<GenerationPass> generationPasses = new ArrayList<>();
        generationPasses.add(Objects.requireNonNull(this.density, "density must be set!"));
        generationPasses.add(Objects.requireNonNull(this.biomes, "biomes must be set!"));
        Collections.addAll(generationPasses, this.decorators);
        Collections.addAll(generationPasses, this.populators);

        for (GenerationBiome biome : biomes) {
            Collections.addAll(generationPasses, biome.getDecorators());
            Collections.addAll(generationPasses, biome.getPopulators());
        }

        PRandom random = new FastPRandom(seed);
        for (GenerationPass pass : generationPasses) {
            pass.init(seed, random.nextLong());
        }
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
        for (int i = 0, x = 0; x < CACHE_X; x++) {
            for (int y = 0; y < CACHE_Y; y++) {
                for (int z = 0; z < CACHE_Z; z++) {
                    densityCache[i++] = density.get(baseX + x * STEP_X, y * STEP_Y, baseZ + z * STEP_Z, biomeMap);
                }
            }
        }

        //interpolate densities and build initial surfaces
        for (int sectionX = 0; sectionX < SAMPLES_X; sectionX++) {
            for (int sectionY = 0; sectionY < SAMPLES_Y; sectionY++) {
                for (int i = (sectionX * CACHE_Y + sectionY) * CACHE_Z, sectionZ = 0; sectionZ < SAMPLES_Z; sectionZ++, i++) {
                    double dxyz = densityCache[i];
                    double dxyZ = densityCache[i + 1];
                    double dxYz = densityCache[i + CACHE_Z];
                    double dxYZ = densityCache[i + CACHE_Z + 1];
                    double dXyz = densityCache[i + CACHE_Y * CACHE_Z];
                    double dXyZ = densityCache[i + CACHE_Y * CACHE_Z + 1];
                    double dXYz = densityCache[i + CACHE_Y * CACHE_Z + CACHE_Z];
                    double dXYZ = densityCache[i + CACHE_Y * CACHE_Z + CACHE_Z + 1];

                    double bx00 = dxyz;
                    double bx01 = dxyZ;
                    double bx10 = dxYz;
                    double bx11 = dxYZ;
                    double gx00 = (dXyz - dxyz) * SCALE_X;
                    double gx01 = (dXyZ - dxyZ) * SCALE_X;
                    double gx10 = (dXYz - dxYz) * SCALE_X;
                    double gx11 = (dXYZ - dxYZ) * SCALE_X;

                    for (int stepX = 0; stepX < STEP_X; stepX++) {
                        double ix00 = bx00 + gx00 * stepX;
                        double ix01 = bx01 + gx01 * stepX;
                        double ix10 = bx10 + gx10 * stepX;
                        double ix11 = bx11 + gx11 * stepX;

                        double by0 = ix00;
                        double by1 = ix01;
                        double gy0 = (ix10 - ix00) * SCALE_Y;
                        double gy1 = (ix11 - ix01) * SCALE_Y;

                        for (int stepY = 0; stepY < STEP_Y; stepY++) {
                            double iy0 = by0 + gy0 * stepY;
                            double iy1 = by1 + gy1 * stepY;

                            double bz = iy0;
                            double gz = (iy1 - iy0) * SCALE_Z;

                            for (int stepZ = 0; stepZ < STEP_Z; stepZ++) {
                                double iz = bz + gz * stepZ;

                                int blockX = sectionX * STEP_X | stepX;
                                int blockY = sectionY * STEP_Y | stepY;
                                int blockZ = sectionZ * STEP_Z | stepZ;

                                if (iz > 0.0d) {
                                    chunk.setBlockRuntimeIdUnsafe(blockX, blockY, blockZ, 0, this.ground);
                                } else if (blockY < this.seaLevel) {
                                    chunk.setBlockRuntimeIdUnsafe(blockX, blockY, blockZ, 0, this.sea);
                                }
                            }
                        }
                    }
                }
            }
        }

        //run decorators and set biomes
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                GenerationBiome biome = biomeMap.get(x | baseX, z | baseZ);
                chunk.setBiome(x, z, biome.getRuntimeId());
                for (Decorator decorator : this.decoratorLookup.computeIfAbsent(biome, this.decoratorLookupComputer)) {
                    decorator.decorate(chunk, random, x, z);
                }
            }
        }

        //clean up
        biomeMap.clear();
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        //porktodo: cache this as well? (reuse cache instead of clearing)
        GenerationBiome biome = this.biomes.get((chunkX << 4) + 7, (chunkZ << 4) + 7); //use biome in middle of chunk for selecting populators
        for (Populator populator : this.populatorsLookup.computeIfAbsent(biome, this.populatorsLookupComputer)) {
            populator.populate(random, level, chunkX, chunkZ);
        }
    }

    @JsonSetter("groundBlock")
    private void setGroundBlock(ConstantBlock block) {
        this.ground = block.runtimeId();
    }

    @JsonSetter("seaBlock")
    private void setSeaBlock(ConstantBlock block) {
        this.sea = block.runtimeId();
    }

    private static final class ThreadData {
        private final double[]        densityCache = new double[CACHE_X * CACHE_Y * CACHE_Z];
        private final CachingBiomeMap biomeMap     = new CachingBiomeMap();
    }
}
