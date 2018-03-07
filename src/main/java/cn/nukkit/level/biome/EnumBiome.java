package cn.nukkit.level.biome;

import cn.nukkit.level.biome.impl.*;

/**
 * @author DaPorkchop_
 *
 * A more effective way of accessing specific biomes (to prevent Biome.getBiome(Biome.OCEAN) and such)
 * Also just looks cleaner than listing everything as static final in {@link Biome}
 */
public enum EnumBiome {
    OCEAN(0, new OceanBiome()),
    PLAINS(1, new PlainsBiome()),
    DESERT(2, new DesertBiome()),
    EXTREME_HILLS(3, new ExtremeHillsBiome()),
    FOREST(4, new ForestBiome()),
    TAIGA(5, new TaigaBiome()),
    SWAMP(6, new SwampBiome()),
    RIVER(7, new RiverBiome()),
    HELL(8, new HellBiome()),
    ICE_PLAINS(12, new IcePlainsBiome()),
    MUSHROOM_ISLAND(14, new MushroomIslandBiome()),
    BEACH(16, new BeachBiome()),
    FOREST_HILLS(18, new ForestHillsBiome()),
    EXTREME_HILLS_EDGE(20, new ExtremeHillsEdgeBiome()),
    JUNGLE(21, new JungleBiome()),
    BIRCH_FOREST(27, new ForestBiome(ForestBiome.TYPE_BIRCH)),
    BIRCH_FOREST_HILLS(28, new ForestHillsBiome(ForestHillsBiome.TYPE_BIRCH)),
    ROOFED_FOREST(29, new RoofedForestBiome()),
    SAVANNA(35, new SavannaBiome()),
    ROOFED_FOREST_M(157, new RoofedForestMBiome());

    public final int id;
    public final Biome biome;

    EnumBiome(int id, Biome biome)  {
        Biome.register(id, biome);
        this.id = id;
        this.biome = biome;
    }

    /**
     * You really shouldn't use this method if you can help it, reference the biomes directly!
     */
    public static Biome getBiome(int id)    {
        return Biome.getBiome(id);
    }
}
