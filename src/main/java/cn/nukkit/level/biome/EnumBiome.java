package cn.nukkit.level.biome;

import cn.nukkit.level.biome.impl.EndBiome;
import cn.nukkit.level.biome.impl.HellBiome;
import cn.nukkit.level.biome.impl.beach.BeachBiome;
import cn.nukkit.level.biome.impl.beach.ColdBeachBiome;
import cn.nukkit.level.biome.impl.desert.DesertBiome;
import cn.nukkit.level.biome.impl.desert.DesertHillsBiome;
import cn.nukkit.level.biome.impl.desert.DesertMBiome;
import cn.nukkit.level.biome.impl.extremehills.*;
import cn.nukkit.level.biome.impl.forest.FlowerForestBiome;
import cn.nukkit.level.biome.impl.forest.ForestBiome;
import cn.nukkit.level.biome.impl.forest.ForestHillsBiome;
import cn.nukkit.level.biome.impl.iceplains.IcePlainsBiome;
import cn.nukkit.level.biome.impl.iceplains.IcePlainsHillsBiome;
import cn.nukkit.level.biome.impl.iceplains.IcePlainsSpikesBiome;
import cn.nukkit.level.biome.impl.jungle.*;
import cn.nukkit.level.biome.impl.mesa.*;
import cn.nukkit.level.biome.impl.mushroom.MushroomIslandBiome;
import cn.nukkit.level.biome.impl.mushroom.MushroomIslandShoreBiome;
import cn.nukkit.level.biome.impl.nether.BasaltDeltasBiome;
import cn.nukkit.level.biome.impl.nether.CrimsonForestBiome;
import cn.nukkit.level.biome.impl.nether.SoulSandValleyBiome;
import cn.nukkit.level.biome.impl.nether.WarpedForestBiome;
import cn.nukkit.level.biome.impl.ocean.*;
import cn.nukkit.level.biome.impl.plains.PlainsBiome;
import cn.nukkit.level.biome.impl.plains.SunflowerPlainsBiome;
import cn.nukkit.level.biome.impl.river.FrozenRiverBiome;
import cn.nukkit.level.biome.impl.river.RiverBiome;
import cn.nukkit.level.biome.impl.roofedforest.RoofedForestBiome;
import cn.nukkit.level.biome.impl.roofedforest.RoofedForestMBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaMBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaPlateauBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaPlateauMBiome;
import cn.nukkit.level.biome.impl.swamp.SwampBiome;
import cn.nukkit.level.biome.impl.swamp.SwamplandMBiome;
import cn.nukkit.level.biome.impl.taiga.*;

/**
 * @author DaPorkchop_
 * <p>
 * A more effective way of accessing specific biomes (to prevent Biome.getBiome(Biome.OCEAN) and such)
 * Also just looks cleaner than listing everything as static final in {@link Biome}
 * </p>
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
    END(9, new EndBiome()),
    FROZEN_OCEAN(10, new FrozenOceanBiome()),
    FROZEN_RIVER(11, new FrozenRiverBiome()),
    ICE_PLAINS(12, new IcePlainsBiome()),
    ICE_MOUNTAINS(13, new IcePlainsHillsBiome()),
    MUSHROOM_ISLAND(14, new MushroomIslandBiome()),
    MUSHROOM_ISLAND_SHORE(15, new MushroomIslandShoreBiome()),
    BEACH(16, new BeachBiome()),
    DESERT_HILLS(17, new DesertHillsBiome()),
    FOREST_HILLS(18, new ForestHillsBiome()),
    TAIGA_HILLS(19, new TaigaHillsBiome()),
    EXTREME_HILLS_EDGE(20, new ExtremeHillsEdgeBiome()),
    JUNGLE(21, new JungleBiome()),
    JUNGLE_HILLS(22, new JungleHillsBiome()),
    JUNGLE_EDGE(23, new JungleEdgeBiome()),
    DEEP_OCEAN(24, new DeepOceanBiome()),
    STONE_BEACH(25, new StoneBeachBiome()),
    COLD_BEACH(26, new ColdBeachBiome()),
    BIRCH_FOREST(27, new ForestBiome(ForestBiome.TYPE_BIRCH)),
    BIRCH_FOREST_HILLS(28, new ForestHillsBiome(ForestHillsBiome.TYPE_BIRCH)),
    ROOFED_FOREST(29, new RoofedForestBiome()),
    COLD_TAIGA(30, new ColdTaigaBiome()),
    COLD_TAIGA_HILLS(31, new ColdTaigaHillsBiome()),
    MEGA_TAIGA(32, new MegaTaigaBiome()),
    MEGA_TAIGA_HILLS(33, new MegaTaigaHillsBiome()),
    EXTREME_HILLS_PLUS(34, new ExtremeHillsPlusBiome()),
    SAVANNA(35, new SavannaBiome()),
    SAVANNA_PLATEAU(36, new SavannaPlateauBiome()),
    MESA(37, new MesaBiome()),
    MESA_PLATEAU_F(38, new MesaPlateauFBiome()),
    MESA_PLATEAU(39, new MesaPlateauBiome()),
    WARM_OCEAN(40, new WarmOceanBiome()),
    DEEP_WARM_OCEAN(41, new WarmDeepOceanBiome()),
    LUKEWARM_OCEAN(42, new LukewarmOceanBiome()),
    DEEP_LUKEWARM_OCEAN(43, new LukewarmDeepOceanBiome()),
    COLD_OCEAN(44, new ColdOceanBiome()),
    DEEP_COLD_OCEAN(45, new ColdDeepOceanBiome()),
    NEW_FROZEN_OCEAN(46, new NewFrozenOceanBiome()),
    DEEP_FROZEN_OCEAN(47, new FrozenDeepOceanBiome()),
    BAMBOO_JUNGLE(48, new BambooJungleBiome()),
    BAMBOO_JUNGLE_HILLS(49, new BambooJungleHillsBiome()),
    // All biomes below this comment are mutated variants of existing biomes
    SUNFLOWER_PLAINS(129, new SunflowerPlainsBiome()),
    DESERT_M(130, new DesertMBiome()),
    EXTREME_HILLS_M(131, new ExtremeHillsMBiome()),
    FLOWER_FOREST(132, new FlowerForestBiome()),
    TAIGA_M(133, new TaigaMBiome()),
    SWAMPLAND_M(134, new SwamplandMBiome()),
    // No, the following jumps in IDs are NOT mistakes
    ICE_PLAINS_SPIKES(140, new IcePlainsSpikesBiome()),
    JUNGLE_M(149, new JungleMBiome()),
    JUNGLE_EDGE_M(151, new JungleEdgeMBiome()),
    BIRCH_FOREST_M(155, new ForestBiome(ForestBiome.TYPE_BIRCH_TALL)),
    BIRCH_FOREST_HILLS_M(156, new ForestHillsBiome(ForestBiome.TYPE_BIRCH_TALL)),
    ROOFED_FOREST_M(157, new RoofedForestMBiome()),
    COLD_TAIGA_M(158, new ColdTaigaMBiome()),
    MEGA_SPRUCE_TAIGA(160, new MegaSpruceTaigaBiome()),
    MEGA_SPRUCE_TAIGA_HILLS(161, new MegaSpruceTaigaHillsBiome()),
    EXTREME_HILLS_PLUS_M(162, new ExtremeHillsPlusMBiome()),
    SAVANNA_M(163, new SavannaMBiome()),
    SAVANNA_PLATEAU_M(164, new SavannaPlateauMBiome()),
    MESA_BRYCE(165, new MesaBryceBiome()),
    MESA_PLATEAU_F_M(166, new MesaPlateauFMBiome()),
    MESA_PLATEAU_M(167, new MesaPlateauMBiome()),

    SOULSAND_VALLEY(178, new SoulSandValleyBiome()),
    CRIMSON_FOREST(179, new CrimsonForestBiome()),
    WARPED_FOREST(180, new WarpedForestBiome()),
    BASALT_DELTAS(181, new BasaltDeltasBiome());

    public final int id;
    public final Biome biome;

    EnumBiome(int id, Biome biome) {
        Biome.register(id, biome);
        this.id = id;
        this.biome = biome;
    }

    /**
     * You really shouldn't use this method if you can help it, reference the biomes directly!
     *
     * @param id biome id
     * @return biome
     */
    @Deprecated
    public static Biome getBiome(int id) {
        return Biome.getBiome(id);
    }

    /**
     * You really shouldn't use this method if you can help it, reference the biomes directly!
     *
     * @param name biome name
     * @return biome
     */
    @Deprecated
    public static Biome getBiome(String name) {
        return Biome.getBiome(name);
    }
}
