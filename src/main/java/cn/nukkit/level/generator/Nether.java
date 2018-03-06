package cn.nukkit.level.generator;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.*;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.*;

public class Nether extends Generator {
    private ChunkManager level;
    /**
     * @var Random
     */
    private NukkitRandom nukkitRandom;
    private Random random;
    private double waterHeight = 32;
    private double emptyHeight = 64;
    private double emptyAmplitude = 1;
    private double density = 0.5;
    private double bedrockDepth = 5;
    private final List<Populator> populators = new ArrayList<>();
    private List<Populator> generationPopulators = new ArrayList<>();

    private long localSeed1;
    private long localSeed2;

    private Simplex noiseBase;

    public Nether() {
        this(new HashMap<>());
    }

    public Nether(Map<String, Object> options) {
        //Nothing here. Just used for future update.
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
        return new HashMap<>();
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.random = new Random();
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.noiseBase = new Simplex(this.nukkitRandom, 26, 3 / 4f, 1 / 64f);
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.localSeed1 = this.random.nextLong();
        this.localSeed2 = this.random.nextLong();
        PopulatorGroundFire groundFire = new PopulatorGroundFire();
        groundFire.setBaseAmount(1);
        groundFire.setRandomAmount(1);
        this.populators.add(groundFire);
        PopulatorLava lava = new PopulatorLava();
        lava.setBaseAmount(0);
        lava.setRandomAmount(2);
        this.populators.add(lava);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        this.nukkitRandom.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ this.level.getSeed());

        double[][][] noise = Generator.getFastNoise3D(this.noiseBase, 16, 128, 16, 4, 8, 4, chunkX * 16, 0, chunkZ * 16);
        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome biome = Biome.getBiome(Biome.HELL);
                int biomeColorAndId = biome.getColor() + (biome.getId() << 24);
                chunk.setBiomeIdAndColor(x, z, biomeColorAndId);

                chunk.setBlockId(x, 0, z, Block.BEDROCK);
                chunk.setBlockId(x, 127, z, Block.BEDROCK);

                for (int y = 1; y <= bedrockDepth; y++) {
                    if (nukkitRandom.nextRange(1, 5) == 1) {
                        chunk.setBlockId(x, y, z, Block.BEDROCK);
                        chunk.setBlockId(x, 127 - y, z, Block.BEDROCK);
                    }
                }
                for (int y = 1; y < 127; ++y) {
                    double noiseValue = (Math.abs(this.emptyHeight - y) / this.emptyHeight) * this.emptyAmplitude - noise[x][z][y];
                    noiseValue -= 1 - this.density;
                    if (noiseValue > 0) {
                        chunk.setBlockId(x, y, z, Block.NETHERRACK);
                    } else if (y <= this.waterHeight) {
                        chunk.setBlockId(x, y, z, Block.STILL_LAVA);
                        chunk.setBlockLight(x, y + 1, z, 15);
                    }
                }
            }
        }
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

    public Vector3 getSpawn() {
        return new Vector3(0, 64, 0);
    }
}