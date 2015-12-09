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
import cn.nukkit.math.Vector3;

import java.util.*;

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

    private cn.nukkit.utils.Random random;
    private int waterHeight = 62;
    private int bedrockDepth = 5;

    private List<Populator> generationPopulators = new ArrayList<>();

    private Simplex noiseBase;
    private Simplex noiseMountain;
    private Simplex noiseOcean;

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
        this.noiseBase = new Simplex(this.random, 4F, 1F / 4F, 1F / 64F);
        this.noiseMountain = new Simplex(this.random, 4F, 1F / 4F, 1F / 110F);
        this.noiseOcean = new Simplex(this.random, 4F, 1F / 4F, 1F / 64F);
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

        double[][] baseNoise = Generator.getFastNoise2D(this.noiseBase, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);
        double[][] oceanNoise = Generator.getFastNoise2D(this.noiseOcean, 16, 16, 8, chunkX * 16, 0, chunkZ * 16);
        double[][] mountainNoise = Generator.getFastNoise2D(this.noiseMountain, 16, 16, 4, chunkX * 16, 0, chunkZ * 16);

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        for(int genx = 0; genx < 16; genx++) {
            for(int genz = 0; genz < 16; genz++) {
                int baseheight = (int) (waterHeight + waterHeight * baseNoise[genx][genz] * 0.05F + 3);
                int oceanheight = (int) (oceanNoise[genx][genz] > 0.5 ? (oceanNoise[genx][genz] - 0.5) * 40 : 0);
                int mountainheight = (int) (mountainNoise[genx][genz] > -0.2 ? (mountainNoise[genx][genz] + 0.2) / 1.2 * 25 : 0);
                int height = baseheight + mountainheight - oceanheight;
                int generatey = height > 127 ? 127 : height > waterHeight ? height : waterHeight;
                Biome biome = this.pickBiome(chunkX * 16 + genx, chunkZ * 16 + genz);
                chunk.setBiomeId(genx, genz, biome.getId());

                for(int geny = 0; geny <= generatey; geny++) {
                    int biomecolor = biome.getColor();
                    //todo: smooth color
                    chunk.setBiomeColor(genx, genz, (biomecolor >> 16), (biomecolor >> 8) & 0xff, (biomecolor & 0xff));
                    if(geny <= bedrockDepth && (geny == 0 || random.nextRange(1,5) == 1)) {
                        chunk.setBlock(genx, geny, genz, Block.BEDROCK);
                    }
                    else if(geny > height) {
                        if((biome.getId() == Biome.ICE_PLAINS || biome.getId() == Biome.TAIGA) && geny == waterHeight) {
                            chunk.setBlock(genx, geny, genz, Block.ICE);
                        }
                        else {
                            chunk.setBlock(genx, geny, genz, Block.STILL_WATER);
                        }
                    }
                    else {
                        chunk.setBlock(genx, geny, genz, Block.STONE);
                    }
                }

            }
        }

        for(Populator populator : this.generationPopulators) {
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
