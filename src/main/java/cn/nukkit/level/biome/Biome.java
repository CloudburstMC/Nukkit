package cn.nukkit.level.biome;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Biome implements BlockID {
    public static final int MAX_BIOMES = 256;
    public static final Biome[] biomes = new Biome[MAX_BIOMES];
    public static final List<Biome> unorderedBiomes = new ArrayList<>();

    private final ArrayList<Populator> populators = new ArrayList<>();
    protected double rainfall = 0.5;
    protected double temperature = 0.5;
    private int id;
    private int minElevation;
    private int maxElevation;

    protected static void register(int id, Biome biome) {
        biome.setId(id);
        biomes[id] = biome;
        unorderedBiomes.add(biome);
    }

    static Biome getBiome(int id) {
        Biome biome = biomes[id];
        return biome != null ? biome : EnumBiome.OCEAN.biome;
    }

    /**
     * Get Biome by name.
     *
     * @param name Name of biome. Name could contain symbol "_" instead of space
     * @return Biome. Null - when biome was not found
     */
    static Biome getBiome(String name) {
        for (Biome biome : biomes) {
            if (biome != null) {
                if (biome.getName().equalsIgnoreCase(name.replace("_", " "))) return biome;
            }
        }
        return null;
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
        if (min > max) {
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

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    //whether or not water should freeze into ice on generation
    public boolean isFreezing() {
        return false;
    }

    /**
     * Whether or not overhangs should generate in this biome (places where solid blocks generate over air)
     *
     * This should probably be used with a custom max elevation or things can look stupid
     */
    public boolean doesOverhang()   {
        return false;
    }
}
