package cn.nukkit.level.generator;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeSelector;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.noise.vanilla.f.NoiseGeneratorOctavesF;
import cn.nukkit.level.generator.noise.vanilla.f.NoiseGeneratorPerlinF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.*;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.*;

/**
 * Nukkit's terrain generator
 * Originally adapted from the PocketMine-MP generator by NycuRO and CreeperFace
 * Mostly rewritten by DaPorkchop_
 * <p>
 * The following classes, and others related to terrain generation are theirs and are intended for NUKKIT USAGE and should not be copied/translated to other server software
 * such as BukkitPE, ClearSky, Genisys, PocketMine-MP, or others
 * <p>
 * Normal.java
 * MushroomPopulator.java
 * DarkOakTreePopulator.java
 * JungleBigTreePopulator.java
 * JungleTreePopulaotr.java
 * SavannaTreePopulator.java
 * SwampTreePopulator.java
 * BasicPopulator.java
 * TreeGenerator.java
 * HugeTreesGenerator.java
 * BeachBiome.java
 * ColdBeachBiome.java
 * DesertBiome.java
 * DesertHillsBiome.java
 * DesertMBiome.java
 * ExtremeHillsBiome.java
 * ExtremeHillsEdgeBiome.java
 * ExtremeHillsMBiome.java
 * ExtremeHillsPlusBiome.java
 * ExtremeHillsPlusMBiome.java
 * StoneBeachBiome.java
 * FlowerForestBiome.java
 * ForestBiome.java
 * ForestHillsBiome.java
 * IcePlainsBiome.java
 * IcePlainsSpikesBiome.java
 * JungleBiome.java
 * JungleEdgeBiome.java
 * JungleEdgeMBiome.java
 * JungleHillsBiome.java
 * JungleMBiome.java
 * MesaBiome.java
 * MesaBryceBiome.java
 * MesaPlateauBiome.java
 * MesaPlateauFBiome.java
 * MesaPlateauFMBiome.java
 * MesaPlateauMBiome.java
 * MushroomIslandBiome.java
 * MushroomIslandShoreBiome.java
 * DeepOceanBiome.java
 * FrozenOceanBiome.java
 * OceanBiome.java
 * PlainsBiome.java
 * SunflowerPlainsBiome.java
 * FrozenRiverBiome.java
 * RiverBiome.java
 * RoofedForestBiome.java
 * RoofedForestMBiome.java
 * SavannaBiome.java
 * SavannaMBiome.java
 * SavannaPlateauBiome.java
 * SavannaPlateauMBiome.java
 * SwampBiome.java
 * SwamplandMBiome.java
 * ColdTaigaBiome.java
 * ColdTaigaHillsBiome.java
 * ColdTaigaMBiome.java
 * MegaSpruceTaigaBiome.java
 * MegaTaigaBiome.java
 * MegaTagaHillsBiome.java
 * TaigaBiome.java
 * TaigaHillsBiome.java
 * TaigaMBiome.java
 * CoveredBiome.java
 * GrassyBiome.java
 * SandyBiome.java
 * WateryBiome.java
 * EnumBiomeBiome.java
 * PopulatorCount.java
 * PopulatorSurfaceBlock.java
 * Normal.java
 * Nether.java
 * End.java
 */
public class Normal extends Generator {
    private static final int SMOOTH_SIZE = 2;
    private static final float[] biomeWeights = new float[25];

    static {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                biomeWeights[i + 2 + (j + 2) * 5] = (float) (10.0F / Math.sqrt((float) (i * i + j * j) + 0.2F));
            }
        }
    }

    private final List<Populator> populators = new ArrayList<>();
    private final List<Populator> generationPopulators = new ArrayList<>();
    private final int seaHeight = 64;
    public NoiseGeneratorOctavesF scaleNoise;
    public NoiseGeneratorOctavesF depthNoise;
    private ChunkManager level;
    private Random random;
    private NukkitRandom nukkitRandom;
    private long localSeed1;
    private long localSeed2;
    private BiomeSelector selector;
    private ThreadLocal<Biome[]> biomes = ThreadLocal.withInitial(() -> new Biome[10 * 10]);
    private ThreadLocal<float[]> depthRegion = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<float[]> mainNoiseRegion = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<float[]> minLimitRegion = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<float[]> maxLimitRegion = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<float[]> heightMap = ThreadLocal.withInitial(() -> new float[825]);
    private NoiseGeneratorOctavesF minLimitPerlinNoise;
    private NoiseGeneratorOctavesF maxLimitPerlinNoise;
    private NoiseGeneratorOctavesF mainPerlinNoise;
    private NoiseGeneratorPerlinF surfaceNoise;

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
        return this.selector.pickBiome(x, z);
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.random = new Random();
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.localSeed1 = this.random.nextLong();
        this.localSeed2 = this.random.nextLong();
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.selector = new BiomeSelector(this.nukkitRandom);

        this.minLimitPerlinNoise = new NoiseGeneratorOctavesF(random, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctavesF(random, 16);
        this.mainPerlinNoise = new NoiseGeneratorOctavesF(random, 8);
        this.surfaceNoise = new NoiseGeneratorPerlinF(random, 4);
        this.scaleNoise = new NoiseGeneratorOctavesF(random, 10);
        this.depthNoise = new NoiseGeneratorOctavesF(random, 16);

        //this should run before all other populators so that we don't do things like generate ground cover on bedrock or something
        PopulatorGroundCover cover = new PopulatorGroundCover();
        this.generationPopulators.add(cover);

        PopulatorBedrock bedrock = new PopulatorBedrock();
        this.generationPopulators.add(bedrock);

        PopulatorCaves caves = new PopulatorCaves();
        this.populators.add(caves);

        PopulatorRavines ravines = new PopulatorRavines();
        this.populators.add(ravines);

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
    public void generateChunk(final int chunkX, final int chunkZ, FullChunk chunk) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        this.nukkitRandom.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ this.level.getSeed());
        Biome[] biomes = this.biomes.get();
        this.selector.getBiomes(biomes, baseX - 2, baseZ - 2);

        //generate base noise values
        float[] depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion.get(), chunkX * 4, chunkZ * 4, 5, 5, 200f, 200f, 0.5f);
        this.depthRegion.set(depthRegion);
        float[] mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion.get(), chunkX * 4, 0, chunkZ * 4, 5, 33, 5, 684.412f / 60f, 684.412f / 160f, 684.412f / 60f);
        this.mainNoiseRegion.set(mainNoiseRegion);
        float[] minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion.get(), chunkX * 4, 0, chunkZ * 4, 5, 33, 5, 684.412f, 684.412f, 684.412f);
        this.minLimitRegion.set(minLimitRegion);
        float[] maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion.get(), chunkX * 4, 0, chunkZ * 4, 5, 33, 5, 684.412f, 684.412f, 684.412f);
        this.maxLimitRegion.set(maxLimitRegion);
        float[] heightMap = this.heightMap.get();

        //generate heightmap and smooth biome heights
        int counter1 = 0;
        int counter2 = 0;
        for (int xSeg = 0; xSeg < 5; xSeg++) {
            for (int zSeg = 0; zSeg < 5; zSeg++) {
                float scaleOffsetSum = 0f;
                float baseHeightSum = 0f;
                float biomeWeightSum = 0f;
                Biome biome = biomes[xSeg + 2 + (zSeg + 2) * 10];

                for (int xSmooth = -SMOOTH_SIZE; xSmooth <= SMOOTH_SIZE; xSmooth++) {
                    for (int zSmooth = -SMOOTH_SIZE; zSmooth <= SMOOTH_SIZE; zSmooth++) {
                        Biome biome1 = biomes[xSeg + xSmooth + 2 + (zSeg + zSmooth + 2) * 10];
                        float baseHeight = biome1.getBaseHeight();
                        float scaleOffset = biome1.getHeightVariation();

                        //TODO: we can implement amplified worlds using this generator!

                        float biomeWeight = biomeWeights[xSmooth + 2 + (zSmooth + 2) * 5] / (baseHeight + 2f);

                        if (baseHeight > biome.getBaseHeight()) {
                            biomeWeight /= 2f;
                        }

                        baseHeightSum += baseHeight;
                        scaleOffsetSum += scaleOffset;
                        biomeWeightSum += biomeWeight;
                    }
                }

                scaleOffsetSum /= biomeWeightSum;
                baseHeightSum /= biomeWeightSum;
                scaleOffsetSum = scaleOffsetSum * 0.9f + 0.1f;
                baseHeightSum = (baseHeightSum * 4f - 1f) / 8f;
                float depthNoise = depthRegion[counter2] / 8000f;

                if (depthNoise < 0f) {
                    depthNoise = -depthNoise * 0.3f;
                }

                depthNoise = depthNoise * 3f - 2f;

                if (depthNoise < 0f) {
                    depthNoise /= 2f;

                    if (depthNoise < -1f) {
                        depthNoise = -1f;
                    }

                    depthNoise /= 1.4f;
                    depthNoise /= 2f;
                } else {
                    if (depthNoise > 1f) {
                        depthNoise = 1f;
                    }

                    depthNoise /= 8f;
                }

                counter2++;

                float baseHeightCopy = baseHeightSum;
                float scaleOffsetCopy = scaleOffsetSum;
                baseHeightCopy = baseHeightCopy + depthNoise * 0.2f;
                baseHeightCopy = baseHeightCopy * 8.5f / 8f;
                float scaleBaseHeight = 8.5f + baseHeightCopy * 4f;

                for (int ySeg = 0; ySeg < 33; ySeg++) {
                    float heightScale = ((float) ySeg - scaleBaseHeight) * 12f * 128f / 256f / scaleOffsetCopy;

                    if (heightScale < 0f) {
                        heightScale *= 4f;
                    }

                    float lowerScale = minLimitRegion[counter1] / 512f;
                    float upperScale = maxLimitRegion[counter1] / 512f;
                    float mainScale = (mainNoiseRegion[counter1] / 10f + 1f) / 2f;
                    float clamp = MathHelper.denormalizeClamp(lowerScale, upperScale, mainScale) - heightScale;

                    if (ySeg > 29) {
                        float yShrink = (ySeg - 29f) / 3f;
                        clamp = clamp * (1f - yShrink) + -10f * yShrink;
                    }

                    heightMap[counter1++] = clamp;
                }
            }
        }

        //place blocks
        for (int xSeg = 0; xSeg < 4; ++xSeg) {
            int xScale = xSeg * 5;
            int xScaleEnd = (xSeg + 1) * 5;

            for (int zSeg = 0; zSeg < 4; ++zSeg) {
                int zScale1 = (xScale + zSeg) * 33;
                int zScaleEnd1 = (xScale + zSeg + 1) * 33;
                int zScale2 = (xScaleEnd + zSeg) * 33;
                int zScaleEnd2 = (xScaleEnd + zSeg + 1) * 33;

                for (int ySeg = 0; ySeg < 32; ++ySeg) {
                    double height1 = heightMap[zScale1 + ySeg];
                    double height2 = heightMap[zScaleEnd1 + ySeg];
                    double height3 = heightMap[zScale2 + ySeg];
                    double height4 = heightMap[zScaleEnd2 + ySeg];
                    double height5 = (heightMap[zScale1 + ySeg + 1] - height1) * 0.125f;
                    double height6 = (heightMap[zScaleEnd1 + ySeg + 1] - height2) * 0.125f;
                    double height7 = (heightMap[zScale2 + ySeg + 1] - height3) * 0.125f;
                    double height8 = (heightMap[zScaleEnd2 + ySeg + 1] - height4) * 0.125f;

                    for (int yIn = 0; yIn < 8; ++yIn) {
                        double d10 = height1;
                        double d11 = height2;
                        double d12 = (height3 - height1) * 0.25f;
                        double d13 = (height4 - height2) * 0.25f;

                        for (int zIn = 0; zIn < 4; ++zIn) {
                            double d16 = (d11 - d10) * 0.25f;
                            double lvt_45_1_ = d10 - d16;

                            for (int xIn = 0; xIn < 4; ++xIn) {
                                if ((lvt_45_1_ += d16) > 0.0f) {
                                    chunk.setBlockId(xSeg * 4 + zIn, ySeg * 8 + yIn, zSeg * 4 + xIn, STONE);
                                } else if (ySeg * 8 + yIn < seaHeight) {
                                    chunk.setBlockId(xSeg * 4 + zIn, ySeg * 8 + yIn, zSeg * 4 + xIn, STILL_WATER);
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        height1 += height5;
                        height2 += height6;
                        height3 += height7;
                        height4 += height8;
                    }
                }
            }
        }

        for (int x = 0; x < 16; x++)    {
            for (int z = 0; z < 16; z++)    {
                Biome biome = selector.pickBiome(baseX | x, baseZ | z);

                chunk.setBiome(x, z, biome);
            }
        }

        //populate chunk
        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ, FullChunk chunk) {
        this.nukkitRandom.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        for (Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }

        Biome biome = EnumBiome.getBiome(chunk.getBiomeId(7, 7));
        biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0.5, 256, 0.5);
    }
}