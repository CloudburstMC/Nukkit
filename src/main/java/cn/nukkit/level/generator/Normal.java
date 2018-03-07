package cn.nukkit.level.generator;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.BiomeSelector;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.*;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.*;

/**
 * This generator was written by DaPorkchop_
 * <p>
 * The following classes are theirs and are intended for NUKKIT USAGE and should not be copied/translated to other software
 * such as BukkitPE, ClearSky, Genisys , Pocketmine-MP
 * <p>
 * Normal.java
 * MushroomPopulator.java
 * DarkOakTreePopulator.java
 * JungleBigTreePopulator.java
 * JungleTreePopulaotr.java
 * SavannaTreePopulator.java
 * SwampTreePopulator.java
 * BasicPopulator.java
 * MesaBiome.java
 * JungleBiome.java
 * SavannaBiome.java
 * RoofedForestBiome.java
 * RoofedForestMBiome.java
 * MushroomIsland.java
 * TreeGenerator.java
 * HugeTreesGenerator.java
 */
public class Normal extends Generator {
    private static final int SMOOTH_SIZE = 2;

    private final List<Populator> populators = new ArrayList<>();
    private final List<Populator> generationPopulators = new ArrayList<>();
    private final int baseNoiseLayers = 2;
    private final int seaHeight = 64;
    private final int bedrockDepth = 5;
    private ChunkManager level;
    private Random random;
    private NukkitRandom nukkitRandom;
    private long localSeed1;
    private long localSeed2;
    private Simplex[] noiseBase = new Simplex[baseNoiseLayers];
    private BiomeSelector selector;
    private ThreadLocal<double[][][]> baseNoise = ThreadLocal.withInitial(() -> new double[5][5][33]);
    private ThreadLocal<double[][]> minHeights = ThreadLocal.withInitial(() -> new double[16][16]);
    private ThreadLocal<double[][]> shrinkFactors = ThreadLocal.withInitial(() -> new double[16][16]);
    private ThreadLocal<Long2ObjectMap<Biome>> biomes = ThreadLocal.withInitial(Long2ObjectOpenHashMap::new);

    public Normal() {
        this(new HashMap<>());
    }

    public Normal(Map<String, Object> options) {
        //Nothing here. Just used for future update.
    }

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
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
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.random = new Random();
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.localSeed1 = this.random.nextLong();
        this.localSeed2 = this.random.nextLong();
        for (int i = 0; i < baseNoiseLayers; i++) {
            this.noiseBase[i] = new Simplex(this.nukkitRandom, 8, 2 / 4F, 1 / ((i + 1) * 64F));
        }
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.selector = new BiomeSelector(this.nukkitRandom);

        PopulatorCaves caves = new PopulatorCaves();
        this.populators.add(caves);

        PopulatorRavines ravines = new PopulatorRavines();
        this.populators.add(ravines);

//        PopulatorDungeon dungeons = new PopulatorDungeon();
//        this.populators.add(dungeons);

        PopulatorGroundCover cover = new PopulatorGroundCover();
        this.generationPopulators.add(cover);

        PopulatorOre ores = new PopulatorOre();
        ores.setOreTypes(new OreType[]{
                new OreType(new BlockOreCoal(), 20, 17, 0, 128),
                new OreType(new BlockOreIron(), 20, 9, 0, 64),
                new OreType(new BlockOreRedstone(), 8, 8, 0, 16),
                new OreType(new BlockOreLapis(), 1, 7, 0, 16),
                new OreType(new BlockOreGold(), 2, 9, 0, 32),
                new OreType(new BlockOreDiamond(), 1, 8, 0, 16),
                new OreType(new BlockDirt(), 10, 33, 0, 128),
                new OreType(new BlockGravel(), 8, 33, 0, 128),
                new OreType(new BlockStone(BlockStone.GRANITE), 10, 33, 0, 80),
                new OreType(new BlockStone(BlockStone.DIORITE), 10, 33, 0, 80),
                new OreType(new BlockStone(BlockStone.ANDESITE), 10, 33, 0, 80)
        });
        //this.populators.add(ores);
    }

    @Override
    public void generateChunk(final int chunkX, final int chunkZ) {
        this.nukkitRandom.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ this.level.getSeed());
        double[][][] baseNoise = this.baseNoise.get();
        Long2ObjectMap<Biome> biomes = this.biomes.get();
        biomes.clear(); //remove anything that may be left over from last chunk
        double[][] minHeights = this.minHeights.get();
        double[][] shrinkFactors = this.shrinkFactors.get();

        //fill noise array
        {
            int xBase = chunkX << 4;
            int zBase = chunkZ << 4;
            //average all noise out
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 33; y++) {
                    for (int z = 0; z < 5; z++) {
                        double val = 0;
                        for (int i = 0; i < baseNoiseLayers; i++) {
                            //another way (average): val += noiseBase[i].noise3D(xBase + (x << 2), y << 3, zBase + (z << 2), true);
                            val = Math.min(val, noiseBase[i].noise3D(xBase + (x << 2), y << 3, zBase + (z << 2), true));
                        }
                        //val /= baseNoiseLayers;
                        baseNoise[x][z][y] = val;
                    }
                }
            }
        }

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        double scaleAmountHoriz = 0.25D;
        double scaleAmountVert = 0.125D;
        //interpolate biome height values
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double maxSum = 0;
                double minSum = 0;
                int i = 0;

                for (int sx = -SMOOTH_SIZE; sx <= SMOOTH_SIZE; sx++) {
                    for (int sz = -SMOOTH_SIZE; sz <= SMOOTH_SIZE; sz++) {
                        Biome biome;
                        long index = Level.chunkHash((chunkX << 4) + x + sx, (chunkZ << 4) + z + sz);
                        if (biomes.containsKey(index)) {
                            biome = biomes.get(index);
                        } else {
                            biome = pickBiome(chunkX * 16 + x + sx, chunkZ * 16 + z + sz);
                            biomes.put(index, biome);
                        }

                        maxSum += biome.getMaxElevation() << 1; //multiply by two
                        minSum += biome.getMinElevation();
                        i++;
                    }
                }

                maxSum /= i;
                minSum /= i;
                minHeights[x][z] = minSum;
                //we use 2 instead of 1 to offset the minimum height to the min value, not have min be the center
                shrinkFactors[x][z] = 2 / (maxSum - minSum);
                chunk.setBiome(x, z, biomes.get(Level.chunkHash((chunkX << 4) | x, (chunkZ << 4) | z)));
            }
        }

        //place blocks
        for (int xPiece = 0; xPiece < 4; xPiece++) {
            for (int zPiece = 0; zPiece < 4; zPiece++) {
                for (int yPiece = 0; yPiece < 32; yPiece++) {
                    double noiseBase1 = baseNoise[xPiece][zPiece][yPiece];
                    double noiseBase2 = baseNoise[xPiece][zPiece + 1][yPiece];
                    double noiseBase3 = baseNoise[xPiece + 1][zPiece][yPiece];
                    double noiseBase4 = baseNoise[xPiece + 1][zPiece + 1][yPiece];
                    double noiseVertIncr1 = (baseNoise[xPiece][zPiece][yPiece + 1] - noiseBase1) * scaleAmountVert;
                    double noiseVertIncr2 = (baseNoise[xPiece][zPiece + 1][yPiece + 1] - noiseBase2) * scaleAmountVert;
                    double noiseVertIncr3 = (baseNoise[xPiece + 1][zPiece][yPiece + 1] - noiseBase3) * scaleAmountVert;
                    double noiseVertIncr4 = (baseNoise[xPiece + 1][zPiece + 1][yPiece + 1] - noiseBase4) * scaleAmountVert;
                    for (int ySeg = 0; ySeg < 8; ySeg++) {
                        double baseOffset = noiseBase1;
                        double baseOffsetMinXMaxZ = noiseBase2;
                        double scaled1 = (noiseBase3 - noiseBase1) * scaleAmountHoriz;
                        double scaled2 = (noiseBase4 - noiseBase2) * scaleAmountHoriz;
                        for (int xSeg = 0; xSeg < 4; xSeg++) {
                            int xLoc = xSeg + xPiece * 4;
                            int yLoc = yPiece * 8 + ySeg;
                            int zLoc = zPiece * 4;
                            double noiseVal = baseOffset;
                            double noiseIncr = (baseOffsetMinXMaxZ - baseOffset) * scaleAmountHoriz;
                            for (int zSeg = 0; zSeg < 4; zSeg++) {
                                double min = minHeights[xLoc][zLoc];
                                double shrinkFactor = shrinkFactors[xLoc][zLoc];
                                int block = Block.AIR;
                                if (noiseVal + ((yLoc - (min)) * shrinkFactor) < 0.0D) {
                                    block = Block.STONE;
                                } else if (yPiece * 8 + ySeg < seaHeight) {
                                    block = Block.STILL_WATER;
                                }
                                chunk.setBlock(xLoc, yLoc, zLoc, block);
                                zLoc++;
                                noiseVal += noiseIncr;
                            }
                            baseOffset += scaled1;
                            baseOffsetMinXMaxZ += scaled2;
                        }
                        noiseBase1 += noiseVertIncr1;
                        noiseBase2 += noiseVertIncr2;
                        noiseBase3 += noiseVertIncr3;
                        noiseBase4 += noiseVertIncr4;
                    }
                }
            }
        }

        //populate chunk
        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom);
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        this.nukkitRandom.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        for (Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom);
        }

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        Biome biome = Biome.getBiome(chunk.getBiomeId(7, 7));
        biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(127.5, 256, 127.5);
    }
}