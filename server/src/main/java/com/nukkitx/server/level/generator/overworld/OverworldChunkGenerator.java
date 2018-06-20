package com.nukkitx.server.level.generator.overworld;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.data.Biome;
import com.nukkitx.api.level.data.GameRule;
import com.nukkitx.server.block.NukkitBlockState;
import com.nukkitx.server.level.biome.decorator.BiomeDecorator;
import com.nukkitx.server.level.biome.decorator.impl.OceanBiome;
import com.nukkitx.server.level.biome.decorator.impl.type.GrassyBiome;
import com.nukkitx.server.level.biome.selector.BiomeSelector;
import com.nukkitx.server.level.biome.selector.overworld.OverworldBiomeSelector;
import com.nukkitx.server.level.generator.AbstractChunkGenerator;
import com.nukkitx.server.level.populator.impl.bedrock.FloorBedrockPopulator;
import gnu.trove.map.hash.TByteObjectHashMap;
import lombok.NonNull;
import net.daporkchop.lib.noise.Noise;
import net.daporkchop.lib.noise.NoiseEngineType;

import java.util.Random;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class OverworldChunkGenerator extends AbstractChunkGenerator {
    private static final BlockState STONE = new NukkitBlockState(BlockTypes.STONE, null, null);
    private static final BlockState WATER = new NukkitBlockState(BlockTypes.STATIONARY_WATER, null, null);
    private static final BlockState AIR = new NukkitBlockState(BlockTypes.AIR, null, null);

    private static final Vector3f SPAWN = new Vector3f(0, 128, 0);

    /**
     * All registered biome decorators
     */
    private static final TByteObjectHashMap<BiomeDecorator> REGISTERED_BIOMES = new TByteObjectHashMap<>();
    /**
     * Stores the biome data during generation. Indexed as ((x + 2) * 20) + z + 2, with ranges for x and z going from -2 to 19
     */
    private static final ThreadLocal<Biome[]> BIOME_CACHE = ThreadLocal.withInitial(() -> null);
    /**
     * Stores noise data during generation
     */
    private static final ThreadLocal<double[]> NOISE_CACHE = ThreadLocal.withInitial(() -> null);
    /**
     * Stores biome height data during generation. Indexed as (x << 4) | z for min heights, ((x << 4) | z) << 8 for max
     */
    private static final ThreadLocal<int[]> MINMAX_CACHE = ThreadLocal.withInitial(() -> null);

    static {
        REGISTERED_BIOMES.put(Biome.OCEAN.id(), new OceanBiome());
        REGISTERED_BIOMES.put(Biome.PLAINS.id(), new GrassyBiome());
    }

    /**
     * The main noise generator
     */
    private volatile Noise noiseGen;
    /**
     * Populates the biome array
     */
    private volatile BiomeSelector selector;

    {
        this.addPopulator(new FloorBedrockPopulator());
    }

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {
        //debug:
        level.getData().getGameRules().setGameRule(GameRule.SHOW_COORDINATES, true);

        super.generateChunk(level, chunk, random);
        int blockX = chunk.getX() << 4;
        int blockZ = chunk.getZ() << 4;
        Biome[] biomes = this.selector.getBiomes(blockX - 2, blockZ - 2, 20, 20, BIOME_CACHE);
        //set biomes
        for (int xx = 15; xx >= 0; xx--) {
            for (int zz = 15; zz >= 0; zz--) {
                chunk.setBiome(xx, zz, biomes[((xx + 2) * 20) + zz + 2]);
            }
        }
        BiomeDecorator decorator;

        int[] minmaxHeight = this.getDataThreadLocal(MINMAX_CACHE, () -> new int[16 * 16 * 2]);
        //calculate min and max height ranges
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
                int min = 0;
                int max = 0;
                int count = 0;
                for (int X = 0; X < 5; X++) {
                    for (int Z = 0; Z < 5; Z++) {
                        decorator = REGISTERED_BIOMES.get(biomes[((x + X) * 20) + z + Z].id());
                        min += decorator.getMinHeight();
                        max += decorator.getMaxHeight();
                        count++;
                    }
                }
                minmaxHeight[(x << 4) | z] = min / count;
                minmaxHeight[((x << 4) | z) << 8] = max / count;
            }
        }
        decorator = REGISTERED_BIOMES.get(biomes[(9 * 20) + 9].id());

        double[] noise = this.getNoise(chunk.getX(), chunk.getZ(), minmaxHeight);
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
            }
        }
    }

    @Override
    protected void init(Level level, long seed) {
        this.noiseGen = new Noise(NoiseEngineType.PERLIN, seed ^ 982375986137953L, 8, 0.01d, 0.25d);

        this.selector = new OverworldBiomeSelector();
        this.selector.init(seed ^ 21298375986113L);
    }

    @Override
    public Vector3f getDefaultSpawn() {
        return SPAWN;
    }

    /**
     * Get the noise data for a given chunk
     *
     * @param chunkX        the chunk's x coordinate
     * @param chunkZ        the chunk's z coordinate
     * @param minMaxHeights the minmaxheight data (see {@link #MINMAX_CACHE}
     * @return a double[] containing noise data for the given chunk
     */
    private double[] getNoise(int chunkX, int chunkZ, @NonNull int[] minMaxHeights) {
        double[] noise = this.getDataThreadLocal(NOISE_CACHE, () -> new double[16 * 16]);
        this.noiseGen.get2d(chunkX << 4, chunkZ << 4, 16, 16, 1.0d, 1.0d, noise);
        //TODO: scale this around a bit or something lol
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
                noise[(x << 5) | z] = (noise[(x << 5) | z] + 1.0d) * 60.0d;
            }
        }
        return noise;
    }

    /**
     * Gets a T from a {@link ThreadLocal}, creating a new instance if the current value is null
     *
     * @param tl       the {@link ThreadLocal} to get from
     * @param supplier provides new instance of T
     * @return a thread local T with the given size
     */
    private <T> T getDataThreadLocal(@NonNull ThreadLocal<T> tl, @NonNull Supplier<T> supplier) {
        T d = tl.get();
        if (d == null) {
            tl.set(d = supplier.get());
        }
        return d;
    }
}
