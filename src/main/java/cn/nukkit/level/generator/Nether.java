package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.*;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.PRandom;

import java.util.*;

public class Nether extends Generator {

    private final boolean legacy;
    private final int version;

    private ChunkManager level;
    private NukkitRandom nukkitRandom;
    private static final int lavaHeight = 32; // should be 31
    private final SimplexF[] noiseGen = new SimplexF[3];
    private final List<Populator> populators = new ArrayList<>();
    private final List<Populator> generationPopulators = new ArrayList<>();

    private long localSeed1;
    private long localSeed2;
    private SimplexF biomeNoise;

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
    private ScaleOctavesOffsetFilter selector;
    private ScaleOctavesOffsetFilter low;
    private ScaleOctavesOffsetFilter high;

    public Nether() {
        this(Collections.emptyMap());
    }

    public Nether(Map<String, Object> options) {
        this.legacy = !options.containsKey("__LevelDB");
        this.version = (int) options.getOrDefault("__Version", 0);
    }

    @Override
    public int getId() {
        return Generator.TYPE_NETHER;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_NETHER;
    }

    @Override
    public String getName() {
        return "nether";
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.nukkitRandom.setSeed(this.level.getSeed());

        SplittableRandom random1 = new SplittableRandom(this.level.getSeed());
        this.localSeed1 = random1.nextLong();
        this.localSeed2 = random1.nextLong();

        if (this.legacy || this.version < 2) {
            for (int i = 0; i < noiseGen.length; i++) {
                noiseGen[i] = new SimplexF(this.nukkitRandom, 4, 1 / 4f, 1 / 64f);
            }
        } else {
            this.selector = new ScaleOctavesOffsetFilter(new PerlinNoiseEngine(PRandom.wrap(new Random(this.level.getSeed()))), 0.01670927734375, 0.0334185546875, 0.01670927734375, 8, 12.75, 0.5);
            this.low = new ScaleOctavesOffsetFilter(new PerlinNoiseEngine(PRandom.wrap(new Random(this.level.getSeed()))), 0.005221649169921875, 0.0078324737548828125, 0.005221649169921875, 16, 1.0, 0);
            this.high = new ScaleOctavesOffsetFilter(new PerlinNoiseEngine(PRandom.wrap(new Random(this.level.getSeed()))), 0.005221649169921875, 0.0078324737548828125, 0.005221649169921875, 16, 1.0, 0);
        }

        this.nukkitRandom.setSeed(this.level.getSeed());

        this.biomeNoise = new SimplexF(this.nukkitRandom, 2F, 1F / 8F, 1F / 2048f);

        PopulatorBedrock bedrock = new PopulatorBedrock(true);
        this.generationPopulators.add(bedrock);

        PopulatorOre ores;
        if (this.legacy) {
            ores = new PopulatorOre(NETHERRACK, new OreType[]{
                    new OreType(Block.get(BlockID.QUARTZ_ORE), 16, 24, 10, 117, NETHERRACK),
                    new OreType(Block.get(BlockID.SOUL_SAND), 12, 23, 0, /*31*/ 105, NETHERRACK),
                    new OreType(Block.get(BlockID.GRAVEL), 2, 64, 5, /*41*/ 105, NETHERRACK),
                    new OreType(Block.get(BlockID.MAGMA), 4, 64, 26, /*37*/ lavaHeight + 1, NETHERRACK),
                    new OreType(Block.get(BlockID.LAVA), 1, 16, 0, lavaHeight, NETHERRACK),
            });
        } else {
            ores = new PopulatorOre(NETHERRACK, new OreType[]{
                    new OreType(Block.get(BlockID.QUARTZ_ORE), 16, 24, 10, 117, NETHERRACK),
                    new OreType(Block.get(BlockID.SOUL_SAND), 12, 23, 0, /*31*/ 105, NETHERRACK),
                    new OreType(Block.get(BlockID.GRAVEL), 2, 64, 5, /*41*/ 105, NETHERRACK),
                    new OreType(Block.get(BlockID.MAGMA), 4, 64, 26, /*37*/ lavaHeight + 1, NETHERRACK),
                    new OreType(Block.get(BlockID.LAVA), 1, 16, 0, lavaHeight, NETHERRACK),
                    new OreType(Block.get(BlockID.NETHER_GOLD_ORE), 10, 16, 10, 117, NETHERRACK),
                    new OreType(Block.get(BlockID.ANCIENT_DEBRIS), 2, 3, 8, 23, NETHERRACK),
                    new OreType(Block.get(BlockID.ANCIENT_DEBRIS), 3, 2, 8, 119, NETHERRACK),
            });
        }
        this.populators.add(ores);

        this.populators.add(new PopulatorNetherFire(FIRE, NETHERRACK));

        PopulatorLava lava = new PopulatorLava();
        lava.setRandomAmount(2);
        this.populators.add(lava);

        this.populators.add(new PopulatorGlowStone());
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        this.nukkitRandom.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ this.level.getSeed());

        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        if (this.version < 2 || !(chunk instanceof LevelDBChunk)) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    chunk.setBiomeId(x, z, EnumBiome.HELL.biome.getId());

                    for (int y = 115; y < 127; ++y) {
                        chunk.setBlockId(x, y, z, Block.NETHERRACK);
                    }

                    for (int y = 1; y < 127; ++y) {
                        if (getNoise(baseX | x, y, baseZ | z) > 0) {
                            chunk.setBlockId(x, y, z, Block.NETHERRACK);
                        } else if (y <= lavaHeight) {
                            chunk.setBlockId(x, y, z, Block.STILL_LAVA);
                            chunk.setBlockLight(x, y + 1, z, 15);
                        }
                    }
                }
            }
        } else { // use generator from Cloudburst
            CoveredBiome[][] biomes = new CoveredBiome[16][16];

            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    CoveredBiome biome = (CoveredBiome) pickBiome(baseX + x, baseZ + z);
                    chunk.setBiomeId(x, z, biome.getId());
                    biomes[x][z] = biome;
                }
            }

            final ThreadData threadData = THREAD_DATA_CACHE.get();
            final double[] densityCache = threadData.densityCache = densityGet(threadData.densityCache, baseX, 0, baseZ);

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

                                    if (blockY < 127 && iz > 0.0d) {
                                        CoveredBiome biome = biomes[blockX][blockZ];
                                        chunk.setFullBlockId(blockX, blockY, blockZ, biome.getGroundId(blockX, 0, blockZ));
                                    } else if (blockY <= lavaHeight) {
                                        chunk.setBlockId(blockX, blockY, blockZ, Block.STILL_LAVA);
                                        chunk.setBlockLight(blockX, blockY + 1, blockZ, 15);
                                    }
                                }
                            }
                        }
                    }

                    i++;
                }

                i += CACHE_Y;
            }

            // Populate ground cover
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    CoveredBiome biome = biomes[x][z];

                    int ground = biome.getGroundId(x, 0, z);
                    int surface = biome.getSurfaceId(x, 0, z);

                    if (ground != surface) {
                        int previous = chunk.getBlockId(x, 126, z);
                        for (int y = 125; y > 1; --y) {
                            int id = chunk.getFullBlock(x, y, z);
                            if (id == ground && previous == AIR) {
                                chunk.setFullBlockId(x, y, z, surface);
                            }
                            previous = id;
                        }
                    }
                }
            }
        }

        // Populate bedrock
        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }
    }

    private double densityGet(int x, int y, int z) {
        if (y >= 128) {
            return 0.0d;
        }

        double selector = NukkitMath.clamp(this.selector.get(x, y, (double) z), 0.0d, 1.0d);
        double low = this.low.get(x, y, (double) z) * NOISE_SCALE_FACTOR;
        double high = this.high.get(x, y, (double) z) * NOISE_SCALE_FACTOR;

        double outputNoise = NukkitMath.lerp(low, high, selector);

        double threshold = y * 0.125d;
        double offset = Math.cos(threshold * Math.PI * 6.0d / 17.0d) * 2.0d;

        if (threshold > 8.0d) {
            threshold = 16.0d - threshold;
        }
        if (threshold < 4.0d) {
            threshold = 4.0d - threshold;
            offset -= threshold * threshold * threshold * 10.0d;
        }

        return outputNoise - offset;
    }

    private double[] densityGet(double[] arr, int x, int y, int z) {
        int totalSize = CACHE_X * CACHE_Y * CACHE_Z;
        if (arr == null || arr.length < totalSize) {
            arr = new double[totalSize];
        }

        for (int i = 0, dx = 0; dx < CACHE_X; dx++) {
            for (int dz = 0; dz < CACHE_Z; dz++) {
                for (int dy = 0; dy < CACHE_Y; dy++) {
                    arr[i++] = densityGet(x + dx * STEP_X, dy * STEP_Y, z + dz * STEP_Z);
                }
            }
        }
        return arr;
    }

    private Biome pickBiome(int x, int z) {
        float value = this.biomeNoise.noise2D(x, z, true);
        float secondaryValue = this.biomeNoise.noise2D(z, x, true); // Reversed x & z is intentional here

        if (value >= 1 / 3f) {
            return secondaryValue >= 0 ? EnumBiome.WARPED_FOREST.biome : EnumBiome.CRIMSON_FOREST.biome;
        } else if (value >= -1 / 3f) {
            return EnumBiome.HELL.biome;
        } else {
            return secondaryValue >= 0 ? EnumBiome.BASALT_DELTAS.biome : EnumBiome.SOULSAND_VALLEY.biome;
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        this.nukkitRandom.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());

        for (Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }

        if (!(chunk instanceof LevelDBChunk)) {
            EnumBiome.HELL.biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
        } else {
            Biome biome = Biome.getBiome(chunk.getBiomeId(7, 7));
            biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
        }
    }

    public Vector3 getSpawn() {
        return new Vector3(0.5, 64, 0.5);
    }

    public float getNoise(int x, int y, int z)  {
        float val = 0f;
        for (int i = 0; i < noiseGen.length; i++)   {
            val += noiseGen[i].noise3D(x >> i, y, z >> i, true);
        }
        return val;
    }

    private static final class ThreadData {
        private double[] densityCache;
    }
}