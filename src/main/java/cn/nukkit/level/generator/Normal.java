package cn.nukkit.level.generator;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.BiomeSelector;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.object.OreType;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.PopulatorGroundCover;
import cn.nukkit.level.generator.populator.PopulatorOre;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Normal extends Generator {

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    private List<Populator> populators = new ArrayList<>();

    private ChunkManager level;

    private NukkitRandom random;

    private List<Populator> generationPopulators = new ArrayList<>();

    private Simplex noiseSeaFloor;

    private BiomeSelector selector;

    private int heightOffset;

    private final int seaHeight = 62;
    private final int seaFloorHeight = 48;
    private final int beathStartHeight = 60;
    private final int beathStopHeight = 64;
    private final int landHeight = 66;
    private final int heightRange = 20;
    private final int bedrockDepth = 5;
    private final int seaFloorGenerateRange = 5;

    public Normal() {
        this(new HashMap<>());
    }

    public Normal(Map<String, Object> options) {
        //Nothing here. Just used for future update.
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
        this.random = random;
        this.random.setSeed(this.level.getSeed());
        this.noiseSeaFloor = new Simplex(this.random, 1F, 1F / 23F, 1F / 30F);
        this.random.setSeed(this.level.getSeed());
        this.selector = new BiomeSelector(this.random, Biome.getBiome(Biome.OCEAN));
        this.heightOffset = random.nextRange(-5, 3);

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

        double[][] baseNoise = Generator.getFastNoise2D(this.noiseSeaFloor, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        for(int genx = 0; genx < 16; genx++) {
            for(int genz = 0; genz < 16; genz++) {

                Biome biome;
                int genyHeight = seaFloorHeight + (int) (seaFloorGenerateRange * baseNoise[genx][genz]);
                //prepare for generate ocean, desert, and land
                if(genyHeight < seaFloorHeight - seaFloorGenerateRange) {
                    genyHeight = seaFloorHeight;
                    biome = Biome.getBiome(Biome.OCEAN);
                }
                else if(genyHeight < beathStartHeight) {
                    biome = Biome.getBiome(Biome.OCEAN);
                }
                else if(genyHeight <= beathStopHeight && genyHeight >= beathStartHeight) {
                    //todo: there is no beach biome, use desert temporarily
                    biome = Biome.getBiome(Biome.DESERT);
                }
                else {
                    if(genyHeight > landHeight) {
                        genyHeight = landHeight;
                    }
                    biome = Biome.getBiome(Biome.PLAINS);
                }
                chunk.setBiomeId(genx, genz, biome.getId());
                //biome color
                //todo: smooth chunk color
                int biomecolor = biome.getColor();
                chunk.setBiomeColor(genx, genz, (biomecolor >> 16), (biomecolor >> 8) & 0xff, (biomecolor & 0xff));
                //generating
                int generateHeight = genyHeight > seaHeight ? genyHeight : seaHeight;
                for(int geny = 0; geny <= generateHeight; geny++) {
                    if (geny <= bedrockDepth && (geny == 0 || random.nextRange(1, 5) == 1)) {
                        chunk.setBlock(genx, geny, genz, Block.BEDROCK);
                    } else if (geny > genyHeight) {
                        if ((biome.getId() == Biome.ICE_PLAINS || biome.getId() == Biome.TAIGA) && geny == seaHeight) {
                            chunk.setBlock(genx, geny, genz, Block.ICE);
                        } else {
                            chunk.setBlock(genx, geny, genz, Block.STILL_WATER);
                        }
                    } else {
                        chunk.setBlock(genx, geny, genz, Block.STONE);
                    }
                }
            }
        }

        //populator chunk
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
