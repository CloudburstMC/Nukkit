package cn.nukkit.level.biome;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Biome implements BlockID {

    public static final int MAX_BIOMES = 256;

    public static final Biome[] biomes = new Biome[Biome.MAX_BIOMES];

    public static final List<Biome> unorderedBiomes = new ArrayList<>();

    private final ArrayList<Populator> populators = new ArrayList<>();

    private int id;

    private float baseHeight = 0.1f;

    private float heightVariation = 0.3f;

    public static Biome getBiome(final int id) {
        final Biome biome = Biome.biomes[id];
        return biome != null ? biome : EnumBiome.OCEAN.biome;
    }

    /**
     * Get Biome by name.
     *
     * @param name Name of biome. Name could contain symbol "_" instead of space
     * @return Biome. Null - when biome was not found
     */
    public static Biome getBiome(final String name) {
        for (final Biome biome : Biome.biomes) {
            if (biome != null) {
                if (biome.getName().equalsIgnoreCase(name.replace("_", " "))) {
                    return biome;
                }
            }
        }
        return null;
    }

    protected static void register(final int id, final Biome biome) {
        biome.setId(id);
        Biome.biomes[id] = biome;
        Biome.unorderedBiomes.add(biome);
    }

    public void clearPopulators() {
        this.populators.clear();
    }

    public void addPopulator(final Populator populator) {
        this.populators.add(populator);
    }

    public void populateChunk(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random) {
        final FullChunk chunk = level.getChunk(chunkX, chunkZ);
        for (final Populator populator : this.populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk);
        }
    }

    public ArrayList<Populator> getPopulators() {
        return this.populators;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public abstract String getName();

    public float getBaseHeight() {
        return this.baseHeight;
    }

    public void setBaseHeight(final float baseHeight) {
        this.baseHeight = baseHeight;
    }

    public float getHeightVariation() {
        return this.heightVariation;
    }

    public void setHeightVariation(final float heightVariation) {
        this.heightVariation = heightVariation;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public boolean equals(final Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    //whether or not water should freeze into ice on generation
    public boolean isFreezing() {
        return false;
    }

    /**
     * Whether or not overhangs should generate in this biome (places where solid blocks generate over air)
     * <p>
     * This should probably be used with a custom max elevation or things can look stupid
     *
     * @return overhang
     */
    public boolean doesOverhang() {
        return false;
    }

    /**
     * How much offset should be added to the min/max heights at this position
     *
     * @param x x
     * @param z z
     * @return height offset
     */
    public int getHeightOffset(final int x, final int z) {
        return 0;
    }

    public boolean canRain() {
        return true;
    }

}
