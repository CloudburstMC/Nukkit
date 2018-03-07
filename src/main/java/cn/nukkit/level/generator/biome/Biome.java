package cn.nukkit.level.generator.biome;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.impl.*;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Biome {
    public static final int OCEAN = 0;
    public static final int PLAINS = 1;
    public static final int DESERT = 2;
    public static final int EXTREME_HILLS = 3;
    public static final int FOREST = 4;
    public static final int TAIGA = 5;
    public static final int SWAMP = 6;
    public static final int RIVER = 7;
    public static final int ICE_PLAINS = 12;
    public static final int MUSHROOM_ISLAND = 14;
    public static final int BEACH = 16;
    public static final int FOREST_HILLS = 18;
    public static final int EXTREME_HILLS_EDGE = 20;
    public static final int JUNGLE = 21;
    public static final int BIRCH_FOREST = 27;
    public static final int BIRCH_FOREST_HILLS = 28;
    public static final int ROOFED_FOREST = 29;
    public static final int SAVANNA = 35;
    public static final int ROOFED_FOREST_M = 157;

    public static final int HELL = 8;

    public static final int MAX_BIOMES = 256;

    public static final Biome[] biomes = new Biome[MAX_BIOMES];
    private final ArrayList<Populator> populators = new ArrayList<>();
    protected double rainfall = 0.5;
    protected double temperature = 0.5;
    protected int grassColor = 0;
    private int id;
    private boolean registered = false;
    private int minElevation;
    private int maxElevation;

    protected static void register(int id, Biome biome) {
        biome.setId(id);
        biome.grassColor = generateBiomeColor(biome.getTemperature(), biome.getRainfall());
        biomes[id] = biome;
    }

    public static void init() {
        register(OCEAN, new OceanBiome());
        register(PLAINS, new PlainBiome());
        register(DESERT, new DesertBiome());
        register(EXTREME_HILLS, new ExtremeHillsBiome());
        register(FOREST, new ForestBiome());
        register(TAIGA, new TaigaBiome());
        register(SWAMP, new SwampBiome());
        register(RIVER, new RiverBiome());
        register(ICE_PLAINS, new IcePlainsBiome());
        register(MUSHROOM_ISLAND, new MushroomIsland());
        register(BEACH, new BeachBiome());
        register(FOREST_HILLS, new ForestHillsBiome());
        register(EXTREME_HILLS_EDGE, new ExtremeHillsEdgeBiome());
        register(JUNGLE, new JungleBiome());
        register(BIRCH_FOREST, new ForestBiome(ForestBiome.TYPE_BIRCH));
        register(BIRCH_FOREST_HILLS, new ForestHillsBiome(ForestHillsBiome.TYPE_BIRCH));
        register(ROOFED_FOREST, new RoofedForestBiome());
        register(SAVANNA, new SavannaBiome());
        register(ROOFED_FOREST_M, new RoofedForestMBiome());

        register(HELL, new HellBiome());
    }

    public static Biome getBiome(int id) {
        Biome biome = biomes[id];
        return biome != null ? biome : biomes[OCEAN];
    }

    /**
     * Get Biome by name.
     *
     * @param name Name of biome. Name could contain symbol "_" instead of space
     * @return Biome. Null - when biome was not found
     */
    public static Biome getBiome(String name) {
        for (Biome biome : biomes) {
            if (biome != null) {
                if (biome.getName().equalsIgnoreCase(name.replace("_", " "))) return biome;
            }
        }
        return null;
    }

    private static int generateBiomeColor(double temperature, double rainfall) {
        double x = (1 - temperature) * 255;
        double z = (1 - rainfall * temperature) * 255;
        double[] c = interpolateColor(256, x, z, new double[]{0x47, 0xd0, 0x33}, new double[]{0x6c, 0xb4, 0x93}, new double[]{0xbf, 0xb6, 0x55}, new double[]{0x80, 0xb4, 0x97});
        return ((int) c[0] << 16) | ((int) c[1] << 8) | (int) (c[2]);
    }

    private static double[] interpolateColor(double size, double x, double z, double[] c1, double[] c2, double[] c3, double[] c4) {
        double[] l1 = lerpColor(c1, c2, x / size);
        double[] l2 = lerpColor(c3, c4, x / size);

        return lerpColor(l1, l2, z / size);
    }

    private static double[] lerpColor(double[] a, double[] b, double s) {
        double invs = 1 - s;
        return new double[]{a[0] * invs + b[0] * s, a[1] * invs + b[1] * s, a[2] * invs + b[2] * s};
    }

    public void clearPopulators() {
        this.populators.clear();
    }

    public void addPopulator(Populator populator) {
        this.populators.add(populator);
    }

    public void populateChunk(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        FullChunk chunk = level.getChunk(chunkX, chunkZ);
        for (Populator populator : populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk);
        }
    }

    public ArrayList<Populator> getPopulators() {
        return populators;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract String getName();

    public int getMinElevation() {
        return minElevation;
    }

    public int getMaxElevation() {
        return maxElevation;
    }

    public void setElevation(int min, int max) {
        if (min > max)  {
            throw new IllegalArgumentException("Min elevation must be less than max!");
        }
        this.minElevation = min;
        this.maxElevation = max;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getRainfall() {
        return rainfall;
    }

    abstract public int getColor();

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }
}
