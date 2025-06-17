package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.noise.engine.SimplexNoiseEngine;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.PRandom;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class TheEnd extends Generator {

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
    private static final double NOISE_SCALE_FACTOR = ((1 << 16) - 1.0d) / 512.0d;
    private static final Ref<ThreadData> THREAD_DATA_CACHE = ThreadRef.soft(ThreadData::new);
    private static final double maxHeightCutoff = 56.0;
    private static final double minHeightCutoff = 32.0;

    private ChunkManager level;
    private ScaleOctavesOffsetFilter selector;
    private ScaleOctavesOffsetFilter low;
    private ScaleOctavesOffsetFilter high;
    private IslandCache islands;

    public TheEnd() {
    }

    public TheEnd(Map<String, Object> options) {
    }

    @Override
    public int getId() {
        return Generator.TYPE_THE_END;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_THE_END;
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return "the_end";
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.selector = new ScaleOctavesOffsetFilter(new PerlinNoiseEngine(PRandom.wrap(new Random(this.level.getSeed()))), 0.008354638671875, 0.008354638671875, 0.008354638671875, 8, 12.75, 0.5);
        this.low = new ScaleOctavesOffsetFilter(new PerlinNoiseEngine(PRandom.wrap(new Random(this.level.getSeed()))), 0.005221649169921875, 0.005221649169921875, 0.005221649169921875, 16, 1.0, 0);
        this.high = new ScaleOctavesOffsetFilter(new PerlinNoiseEngine(PRandom.wrap(new Random(this.level.getSeed()))), 0.005221649169921875, 0.005221649169921875, 0.005221649169921875, 16, 1.0, 0);

        this.islands = new IslandCache();
        this.islands.init(PRandom.wrap(new Random(this.level.getSeed())));
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                chunk.setBiomeId(x, z, EnumBiome.END.biome.getId());
            }
        }

        final ThreadData threadData = THREAD_DATA_CACHE.get();
        final double[] densityCache = threadData.densityCache = densityGet(threadData.densityCache, baseX, baseZ);

        for (int i = 0, sectionX = 0; sectionX < SAMPLES_X; sectionX++) {
            for (int sectionZ = 0; sectionZ < SAMPLES_Z; sectionZ++) {
                for (int sectionY = 0; sectionY < SAMPLES_Y; sectionY++, i++) {
                    double dxyz = densityCache[i];
                    double dxYz = densityCache[i + 1];
                    double dxyZ = densityCache[i + CACHE_Y];
                    double dxYZ = densityCache[i + CACHE_Y + 1];
                    double dXyz = densityCache[i + CACHE_Y * CACHE_Z];
                    double dXYz = densityCache[i + CACHE_Y * CACHE_Z + 1];
                    double dXyZ = densityCache[i + CACHE_Y * CACHE_Z + CACHE_Y];
                    double dXYZ = densityCache[i + CACHE_Y * CACHE_Z + CACHE_Y + 1];

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

                                if (iz > 0.0d && blockY > 16) { // TODO: fix
                                    chunk.setBlockId(blockX, blockY, blockZ, Block.END_STONE);
                                }
                            }
                        }
                    }
                }

                i++;
            }

            i += CACHE_Y;
        }
    }

    private double[] densityGet(double[] arr, int x, int z) {
        int totalSize = CACHE_X * CACHE_Y * CACHE_Z;
        if (arr == null || arr.length < totalSize) {
            arr = new double[totalSize];
        }

        for (int i = 0, dx = 0, xx = x; dx < CACHE_X; dx++, xx += STEP_X) {
            for (int dz = 0, zz = z; dz < CACHE_Z; dz++, zz += STEP_Z) {
                double islandNoise = this.islands.get(xx, zz) - 8.0d;

                for (int dy = 0, yy = 0; dy < CACHE_Y; dy++, yy += STEP_Y) {
                    double selector = NukkitMath.clamp(this.selector.get(xx, yy, (double) zz), 0.0d, 1.0d);
                    double low = this.low.get(xx, yy, (double) zz) * NOISE_SCALE_FACTOR;
                    double high = this.high.get(xx, yy, (double) zz) * NOISE_SCALE_FACTOR;

                    arr[i++] = this.cutOff(yy, NukkitMath.lerp(low, high, selector) + islandNoise);
                }
            }
        }
        return arr;
    }

    private double cutOff(double y, double noise) {
        if (y > maxHeightCutoff) {
            double factor = NukkitMath.clamp(((y * 0.125d) - (minHeightCutoff * 0.125d)) * 0.015625d, 0.0d, 1.0d);
            return noise * (1.0d - factor) - 3000.0d * factor;
        } else if (y < minHeightCutoff) {
            if (y < 16) {
                return 0.0d;
            }
            double factor = ((minHeightCutoff * 0.125d) - (y * 0.125d)) / ((minHeightCutoff * 0.125d) - 1.0d);
            return noise * (1.0d - factor) - 30.0d * factor;
        } else {
            return noise;
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
    }

    public Vector3 getSpawn() {
        return new Vector3(100.5, 49, 0.5);
    }

    private static final class ThreadData {
        private double[] densityCache;
    }

    private static class IslandCache {

        private static final long NaN = Double.doubleToRawLongBits(Double.NaN);

        private final Ref<Long2LongLinkedOpenHashMap> cacheCache = ThreadRef.soft(Long2LongLinkedOpenHashMap::new);

        private double get(int x, int z) {
            Long2LongLinkedOpenHashMap cache = this.cacheCache.get();
            long val = cache.getOrDefault(Level.chunkHash(x, z), NaN);
            if (val == NaN) {
                if (cache.size() >= 1024) {
                    cache.removeFirstLong();
                }

                cache.put(Level.chunkHash(x, z), val = Double.doubleToRawLongBits(this.computeValue(x, z)));
            }
            return Double.longBitsToDouble(val);
        }

        private NoiseSource island;
        private NoiseSource weight;

        private void init(PRandom random) {
            this.island = new ScaleOctavesOffsetFilter(new SimplexNoiseEngine(PRandom.wrap(new Random(random.nextLong()))), 1, 1, 1, 1, 1, 0);
            this.weight = new ScaleOctavesOffsetFilter(new SimplexNoiseEngine(PRandom.wrap(new Random(random.nextLong()))), 1, 1, 1, 1, 6.5, 15.5);
        }

        private double computeValue(int x, int z) {
            final double islandRadius = 100.0;
            final double outerIslandStartRadiusSq = (1024.0 / 16.0d) * (1024.0 / 16.0d);
            final double outerIslandSeedThreshold = -0.8;

            final double chunkX = x >> 4;
            final double chunkZ = z >> 4;
            final double tileX = (x & 0xF) * 0.125d;
            final double tileZ = (z & 0xF) * 0.125d;

            double val = islandRadius - Math.sqrt((x * 0.125d) * (x * 0.125d) + (z * 0.125d) * (z * 0.125d)) * 8.0d;

            for (int dx = -12; dx <= 12; dx++) {
                for (int dz = -12; dz <= 12; dz++) {
                    double islandX = chunkX + dx;
                    double islandZ = chunkZ + dz;

                    if (islandX * islandX + islandZ * islandZ > outerIslandStartRadiusSq && this.island.get(islandX, islandZ) < outerIslandSeedThreshold) {
                        double weight = this.weight.get(islandX, islandZ);

                        double offsetX = tileX - dx * 2.0d;
                        double offsetZ = tileZ - dz * 2.0d;

                        val = Math.max(val, islandRadius - Math.sqrt(offsetX * offsetX + offsetZ * offsetZ) * weight);
                    }
                }
            }

            return NukkitMath.clamp(val, -100.0d, 80.0d);
        }
    }
}
