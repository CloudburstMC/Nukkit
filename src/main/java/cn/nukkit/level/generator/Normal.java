package cn.nukkit.level.generator;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.BiomeSelector;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.PopulatorCaves;
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
    private Simplex noiseLand;
    private Simplex noiseMountains;
    private Simplex noiseBaseGround;
    private Simplex noiseRiver;

    private BiomeSelector selector;

    private int heightOffset;

    private final int seaHeight = 62;
    private final int seaFloorHeight = 48;
    private final int beathStartHeight = 60;
    private final int beathStopHeight = 64;
    private final int bedrockDepth = 5;
    private final int seaFloorGenerateRange = 5;
    private final int landHeightRange = 18; // 36 / 2
    private final int mountainHeight = 13; // 26 / 2
    private final int basegroundHeight = 3;

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
        this.noiseSeaFloor = new Simplex(this.random, 1F, 1F / 8F, 1F / 64F);
        this.noiseLand = new Simplex(this.random, 2F, 1F / 8F, 1F / 512F);
        this.noiseMountains = new Simplex(this.random, 4F, 1F, 1F / 500F);
        this.noiseBaseGround = new Simplex(this.random, 4F, 1F / 4F, 1F / 64F);
        this.noiseRiver = new Simplex(this.random, 4F, 1F / 4F, 1F / 128F);
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

        PopulatorCaves caves = new PopulatorCaves();
        this.populators.add(caves);

        PopulatorOre ores = new PopulatorOre();
        ores.setOreTypes(new OreType[]{
                new OreType(new BlockOreCoal(), 20, 16, 0, 128),
                new OreType(new BlockOreIron(), 20, 8, 0, 64),
                new OreType(new BlockOreRedstone(), 8, 7, 0, 16),
                new OreType(new BlockOreLapis(), 1, 6, 0, 32),
                new OreType(new BlockOreGold(), 2, 8, 0, 32),
                new OreType(new BlockOreDiamond(), 1, 7, 0, 16),
                new OreType(new BlockDirt(), 20, 32, 0, 128),
                new OreType(new BlockGravel(), 10, 16, 0, 128)
        });
        this.populators.add(ores);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        this.random.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());

        double[][] seaFloorNoise = Generator.getFastNoise2D(this.noiseSeaFloor, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);
        double[][] landNoise = Generator.getFastNoise2D(this.noiseLand, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);
        double[][] mountainNoise = Generator.getFastNoise2D(this.noiseMountains, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);
        double[][] baseNoise = Generator.getFastNoise2D(this.noiseBaseGround, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);
        double[][] riverNoise = Generator.getFastNoise2D(this.noiseRiver, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        for (int genx = 0; genx < 16; genx++) {
            for (int genz = 0; genz < 16; genz++) {

                Biome biome;
                boolean canBaseGround = false;
                boolean canRiver = true;

                //using a quadratic function which smooth the world
                //y = (2.956x)^2 - 0.6,  (0 <= x <= 2)
                double landHeightNoise = landNoise[genx][genz] + 1F;
                landHeightNoise *= 2.956;
                landHeightNoise = landHeightNoise * landHeightNoise;
                landHeightNoise = landHeightNoise - 0.6F;
                landHeightNoise = landHeightNoise > 0 ? landHeightNoise : 0;

                //generate mountains
                double mountainHeightGenerate = mountainNoise[genx][genz] - 0.2F;
                mountainHeightGenerate = mountainHeightGenerate > 0 ? mountainHeightGenerate : 0;
                int mountainGenerate = (int) (mountainHeight * mountainHeightGenerate);

                int landHeightGenerate = (int) (landHeightRange * landHeightNoise);
                if (landHeightGenerate > landHeightRange) {
                    if (landHeightGenerate > landHeightRange) {
                        canBaseGround = true;
                    }
                    landHeightGenerate = landHeightRange;
                }

                int genyHeight = seaFloorHeight + landHeightGenerate;
                genyHeight += mountainGenerate;

                //prepare for generate ocean, desert, and land
                if (genyHeight < beathStartHeight) {
                    if (genyHeight < beathStartHeight - 5) {
                        genyHeight += (int) (seaFloorGenerateRange * seaFloorNoise[genx][genz]);
                    }
                    biome = Biome.getBiome(Biome.OCEAN);
                    if (genyHeight < seaFloorHeight - seaFloorGenerateRange) {
                        genyHeight = seaFloorHeight;
                    }
                    canRiver = false;
                } else if (genyHeight <= beathStopHeight && genyHeight >= beathStartHeight) {
                    //todo: there is no beach biome, use desert temporarily
                    biome = Biome.getBiome(Biome.DESERT);
                } else {
                    biome = this.pickBiome(chunkX * 16 + genx, chunkZ * 16 + genz);
                    if (canBaseGround) {
                        int baseGroundHeight = (int) (landHeightRange * landHeightNoise) - landHeightRange;
                        int baseGroundHeight2 = (int) (basegroundHeight * (baseNoise[genx][genz] + 1F));
                        if (baseGroundHeight2 > baseGroundHeight) baseGroundHeight2 = baseGroundHeight;
                        if (baseGroundHeight2 > mountainGenerate)
                            baseGroundHeight2 = baseGroundHeight2 - mountainGenerate;
                        else baseGroundHeight2 = 0;
                        genyHeight += baseGroundHeight2;
                    }
                }
                if (canRiver && genyHeight <= seaHeight - 5) {
                    canRiver = false;
                }
                //generate river
                if (canRiver) {
                    double riverGenerate = riverNoise[genx][genz];
                    if (riverGenerate > -0.25F && riverGenerate < 0.25F) {
                        riverGenerate = riverGenerate > 0 ? riverGenerate : -riverGenerate;
                        riverGenerate = 0.25F - riverGenerate;
                        //y=x^2 * 4 - 0.0000001
                        riverGenerate = riverGenerate * riverGenerate * 4F;
                        //smooth again
                        riverGenerate = riverGenerate - 0.0000001F;
                        riverGenerate = riverGenerate > 0 ? riverGenerate : 0;
                        genyHeight -= riverGenerate * 64;
                        if (genyHeight < seaHeight) {
                            biome = Biome.getBiome(Biome.RIVER);
                            //to generate river floor
                            if (genyHeight <= seaHeight - 8) {
                                int genyHeight1 = seaHeight - 9 + (int) (basegroundHeight * (baseNoise[genx][genz] + 1F));
                                int genyHeight2 = genyHeight < seaHeight - 7 ? seaHeight - 7 : genyHeight;
                                genyHeight = genyHeight1 > genyHeight2 ? genyHeight1 : genyHeight2;
                            }
                        }
                    }
                }
                chunk.setBiomeId(genx, genz, biome.getId());
                //biome color
                //todo: smooth chunk color
                int biomecolor = biome.getColor();
                chunk.setBiomeColor(genx, genz, (biomecolor >> 16), (biomecolor >> 8) & 0xff, (biomecolor & 0xff));
                //generating
                int generateHeight = genyHeight > seaHeight ? genyHeight : seaHeight;
                for (int geny = 0; geny <= generateHeight; geny++) {
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
