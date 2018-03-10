package cn.nukkit.level.generator;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeSelector;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.noise.SimplexF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.*;
import cn.nukkit.level.generator.populator.type.Populator;
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
    private static final int SMOOTH_SIZE = 3 << 1;
    private static final int STONE = BlockID.STONE << 4;
    private static final int STILL_WATER = BlockID.STILL_WATER << 4;

    private final List<Populator> populators = new ArrayList<>();
    private final List<Populator> generationPopulators = new ArrayList<>();
    private final int baseNoiseLayers = 3;
    private final int seaHeight = 64;
    private ChunkManager level;
    private Random random;
    private NukkitRandom nukkitRandom;
    private long localSeed1;
    private long localSeed2;
    private SimplexF[] noiseBase = new SimplexF[baseNoiseLayers];
    private BiomeSelector selector;
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
        for (int i = 0; i < baseNoiseLayers; i++) {
            this.noiseBase[i] = new SimplexF(this.nukkitRandom, 4, 2 / 4F, 1 / ((i + 1) * 64F));
        }
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.selector = new BiomeSelector(this.nukkitRandom);

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
        Long2ObjectMap<Biome> biomes = this.biomes.get();

        //smooth biome height values
        int xPos = baseX;
        for (int x = 0; x < 16; x++, xPos++) {
            int zPos = baseZ;
            for (int z = 0; z < 16; z++, zPos++) {
                int maxSum = 0;
                int minSum = 0;
                int count = 0;

                for (int sx = -SMOOTH_SIZE; sx <= SMOOTH_SIZE; sx += 2) {
                    for (int sz = -SMOOTH_SIZE; sz <= SMOOTH_SIZE; sz += 2) {
                        Biome biome;
                        long index = Level.chunkHash(xPos + sx, zPos + sz);
                        if (biomes.containsKey(index)) {
                            biome = biomes.get(index);
                        } else {
                            biome = pickBiome(xPos + sx, zPos + sz);
                            biomes.put(index, biome);
                        }

                        maxSum += biome.getMaxElevation() << 1; //multiply by two
                        minSum += biome.getMinElevation();
                        count++;
                    }
                }

                maxSum /= count;
                minSum /= count;
                //we use 2 instead of 1 to offset the minimum height to the min value, not have min be the center
                float shrinkFactor = 2f / (float) (maxSum - minSum);
                Biome biome = biomes.get(Level.chunkHash(xPos, zPos));
                chunk.setBiome(x, z, biome);

                //place blocks
                //we use two different methods depending on whether or not there'll be overhangs, helping us optimize everything a lot
                if (biome.doesOverhang())   {
                    //iterate from the max height to the min height
                    //this doesn't fill stone blocks to the bottom, meaning overhangs can spawn
                    //it also means that more noise samples need to be taken, though
                    for (int y = maxSum; y > minSum; y--)   {
                        int block = 0;
                        float noise = getNoiseAt(xPos, y, zPos) + (y - minSum) * shrinkFactor;
                        if (noise < 0f) {
                            block = STONE;
                        } else if (y < seaHeight) {
                            block = STILL_WATER;
                        }

                        if (block != 0) {
                            chunk.setFullBlockId(x, y, z, block);
                        }
                    }
                } else {
                    boolean flag = false;
                    //iterate from the max height to the min height
                    //as soon as we find a block that isn't air, set every block below that to stone
                    //this prevents overhangs from spawning in biomes where they shouldn't
                    for (int y = maxSum; y > minSum; y--)   {
                        if (flag)   {
                            chunk.setFullBlockId(x, y, z, STONE);
                        } else {
                            int block = 0;
                            float noise = getNoiseAt(xPos, y, zPos) + (y - minSum) * shrinkFactor;
                            if (noise < 0f) {
                                block = STONE;
                                flag = true;
                            } else if (y < seaHeight) {
                                block = STILL_WATER;
                            }

                            if (block != 0) {
                                chunk.setFullBlockId(x, y, z, block);
                            }
                        }
                    }
                }
                //everything below the min height can be safely filled with stone
                //we don't need to fill everything above the max height with air because all blocks default to that
                for (int y = minSum; y > -1; y--)   {
                    chunk.setFullBlockId(x, y, z, STONE);
                }
            }
        }
        biomes.clear(); //remove all temporary data because we don't need it

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
        return new Vector3(127.5, 256, 127.5);
    }

    public float getNoiseAt(int x, int y, int z)    {
        float val = 0;
        for (int i = 0; i < baseNoiseLayers; i++) {
            //another way (average): val += noiseBase[i].noise3D(xBase + (x << 2), y << 3, zBase + (z << 2), true);
            val = Math.min(val, noiseBase[i].noise3D(x, y, z, true));
        }
        //val /= baseNoiseLayers;
        return val;
    }
}