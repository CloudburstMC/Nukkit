package cn.nukkit.level.generator.normal;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.BiomeSelector;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.object.OreType;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.PopulatorGroundCover;
import cn.nukkit.level.generator.populator.PopulatorOre;
import cn.nukkit.math.Vector3;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Normal extends Generator {

    private List<Populator> populators = new ArrayList<>();

    private ChunkManager level;

    private cn.nukkit.utils.Random random;
    private int waterHeight = 62;
    private int bedrockDepth = 5;

    private List<Populator> generationPopulators = new ArrayList<>();

    private Simplex noiseBase;

    private BiomeSelector selector;

    private static Map<Integer, Map<Integer, Double>> GAUSSIAN_KERNEL;
    private static int SMOOTH_SIZE = 2;

    public Normal() {
        this(new HashMap<>());
    }

    public Normal(Map<String, Object> options) {
        if (GAUSSIAN_KERNEL == null) {
            generateKernel();
        }
    }

    public static void generateKernel() {
        GAUSSIAN_KERNEL = new HashMap<>();

        double bellSize = 1d / SMOOTH_SIZE;
        double bellHeight = 2d * SMOOTH_SIZE;

        for (int sx = -SMOOTH_SIZE; sx <= SMOOTH_SIZE; ++sx) {
            GAUSSIAN_KERNEL.put(sx + SMOOTH_SIZE, new HashMap<>());

            for (int sz = -SMOOTH_SIZE; sz <= SMOOTH_SIZE; ++sz) {
                double bx = bellSize * sx;
                double bz = bellSize * sz;
                GAUSSIAN_KERNEL.get(sx + SMOOTH_SIZE).put(sz + SMOOTH_SIZE, bellHeight * Math.exp(-(bx * bx + bz * bz) / 2));
            }
        }
    }

    @Override
    public String getName() {
        return "normal";
    }

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    public Biome pickBiome(int x, int z) {
        long hash = x * 2345803 ^ z * 9236449 ^ this.level.getSeed();
        hash *= hash + 223;

        long xNoise = hash >> 20 & 3;
        long zNoise = hash >> 22 & 3;

        if (xNoise == 3) {
            xNoise = 1;
        }
        if (zNoise == 3) {
            zNoise = 1;
        }

        return this.selector.pickBiome(x + xNoise - 1, z + zNoise - 1);
    }

    @Override
    public void init(ChunkManager level, cn.nukkit.utils.Random random) {
        this.level = level;
        this.random = random;
        this.random.setSeed(this.level.getSeed());
        this.noiseBase = new Simplex(this.random, 4, 1 / 4, 1 / 32);
        this.random.setSeed(this.level.getSeed());
        this.selector = new BiomeSelector(this.random, Biome.getBiome(Biome.OCEAN));

        this.selector.addBiome(Biome.getBiome(Biome.OCEAN));
        this.selector.addBiome(Biome.getBiome(Biome.PLAINS));
        this.selector.addBiome(Biome.getBiome(Biome.DESERT));
        this.selector.addBiome(Biome.getBiome(Biome.MOUNTAINS));
        this.selector.addBiome(Biome.getBiome(Biome.FOREST));
        this.selector.addBiome(Biome.getBiome(Biome.TAIGA));
        this.selector.addBiome(Biome.getBiome(Biome.SWAMP));
        this.selector.addBiome(Biome.getBiome(Biome.RIVER));
        this.selector.addBiome(Biome.getBiome(Biome.ICE_PLAINS));
        this.selector.addBiome(Biome.getBiome(Biome.SMALL_MOUNTAINS));
        this.selector.addBiome(Biome.getBiome(Biome.BIRCH_FOREST));

        this.selector.recalculate();

        PopulatorGroundCover cover = new PopulatorGroundCover();
        this.generationPopulators.add(cover);

        PopulatorOre ores = new PopulatorOre();
        ores.setOreTypes(new OreType[]{
                new OreType(new CoalOre(), 20, 16, 0, 128),
                new OreType(new IronOre(), 20, 8, 0, 64),
                new OreType(new RedstoneOre(), 8, 7, 0, 16),
                new OreType(new LapisOre(), 1, 6, 0, 32),
                new OreType(new GoldOre(), 2, 8, 0, 32),
                new OreType(new DiamondOre(), 1, 7, 0, 16),
                new OreType(new Dirt(), 20, 32, 0, 128),
                new OreType(new Gravel(), 10, 16, 0, 128)
        });
        this.populators.add(ores);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        this.random.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());

        double[][][] noise = Generator.getFastNoise3D(this.noiseBase, 16, 128, 16, 4, 8, 4, chunkX * 16, 0, chunkZ * 16);

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        Map<String, Biome> biomeCache = new HashMap<>();

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                double minSum = 0;
                double maxSum = 0;
                double weightSum = 0;

                Biome biome = this.pickBiome(chunkX * 16 + x, chunkZ * 16 + z);
                chunk.setBiomeId(x, z, biome.getId());
                int[] color = {0, 0, 0};

                for (int sx = -SMOOTH_SIZE; sx <= SMOOTH_SIZE; ++sx) {
                    for (int sz = -SMOOTH_SIZE; sz <= SMOOTH_SIZE; ++sz) {

                        double weight = GAUSSIAN_KERNEL.get(sx + SMOOTH_SIZE).get(sz + SMOOTH_SIZE);

                        Biome adjacent;
                        if (sx == 0 && sz == 0) {
                            adjacent = biome;
                        } else {
                            String index = Level.chunkHash(chunkX * 16 + x + sx, chunkZ * 16 + z + sz);
                            if (biomeCache.containsKey(index)) {
                                adjacent = biomeCache.get(index);
                            } else {
                                biomeCache.put(index, adjacent = this.pickBiome(chunkX * 16 + x + sx, chunkZ * 16 + z + sz));
                            }
                        }

                        minSum += (adjacent.getMinElevation() - 1) * weight;
                        maxSum += adjacent.getMaxElevation() * weight;
                        int bColor = adjacent.getColor();
                        color[0] += ((bColor >> 16) * (bColor >> 16)) * weight;
                        color[1] += (((bColor >> 8) & 0xff) * ((bColor >> 8) & 0xff)) * weight;
                        color[2] += ((bColor & 0xff) * (bColor & 0xff)) * weight;

                        weightSum += weight;
                    }
                }

                minSum /= weightSum;
                maxSum /= weightSum;

                chunk.setBiomeColor(x, z, (int) Math.sqrt(color[0] / weightSum), (int) Math.sqrt(color[1] / weightSum), (int) Math.sqrt(color[2] / weightSum));

                double smoothHeight = (maxSum - minSum) / 2;

                for (int y = 0; y < 128; ++y) {
                    if (y == 0) {
                        chunk.setBlockId(x, y, z, Block.BEDROCK);
                        continue;
                    }
                    double noiseValue = noise[x][z][y] - 1 / smoothHeight * (y - smoothHeight - minSum);

                    if (noiseValue > 0) {
                        chunk.setBlockId(x, y, z, Block.STONE);
                    } else if (y <= this.waterHeight) {
                        chunk.setBlockId(x, y, z, Block.STILL_WATER);
                    }
                }
            }
        }

        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.random);
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        this.random.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        for (Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.random);
        }

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        Biome biome = Biome.getBiome(chunk.getBiomeId(7, 7));
        biome.populateChunk(this.level, chunkX, chunkZ, this.random);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(127.5, 128, 127.5);
    }
}
