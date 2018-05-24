package com.nukkitx.server.level.biome;

import com.nukkitx.api.level.data.Biome;
import gnu.trove.map.hash.TByteObjectHashMap;
import lombok.Getter;

public enum NukkitBiome {
    OCEAN(0, "Ocean"),
    PLAINS(1, "Plains"),
    DESERT(2, "Desert"),
    EXTREME_HILLS(3, "Extreme Hills"),
    FOREST(4, "Forest"),
    TAIGA(5, "Taiga"),
    SWAMPLAND(6, "Swampland"),
    RIVER(7, "River"),
    HELL(8, "Hell"),
    SKY(9, "The End"),
    FROZEN_OCEAN(10, "FrozenOcean"),
    FROZEN_RIVER(11, "FrozenRiver"),
    ICE_FLATS(12, "Ice Plains"),
    ICE_MOUNTAINS(13, "Ice Mountains"),
    MUSHROOM_ISLAND(14, "Mushroom Island"),
    MUSHROOM_ISLAND_SHORE(15, "Mushroom Island Shore"),
    BEACHES(16, "Beach"),
    DESERT_HILLS(17, "Desert Hills"),
    FOREST_HILLS(18, "Forest Hills"),
    TAIGA_HILLS(19, "Taiga Hills"),
    SMALLER_EXTREME_HILLS(20, "Extreme Hills Edge"),
    JUNGLE(21, "Jungle"),
    JUNGLE_HILLS(22, "JungleHills"),
    JUNGLE_EDGE(23, "JungleEdge"),
    DEEP_OCEAN(24, "Deep Ocean"),
    STONE_BEACH(25, "Stone Beach"),
    COLD_BEACH(26, "Cold Beach"),
    BIRCH_FOREST(27, "Birch Forest"),
    BIRCH_FOREST_HILLS(28, "Birch Forest Hills"),
    ROOFED_FOREST(29, "Roofed Forest"),
    TAIGA_COLD(30, "Cold Taiga"),
    TAIGA_COLD_HILLS(31, "Cold Taiga Hills"),
    REDWOOD_TAIGA(32, "Mega Taiga"),
    REDWOOD_TAIGA_HILLS(33, "Mega Taiga Hills"),
    EXTREME_HILLS_WITH_TREES(34, "Extreme Hills+"),
    SAVANNA(35, "Savanna"),
    SAVANNA_ROCK(36, "Savanna Plateau"),
    MESA(37, "Mesa"),
    MESA_ROCK(38, "Mesa Plateau F"),
    MESA_CLEAR_ROCK(39, "Mesa Plateau"),
    SKY_ISLAND_LOW(40, "The End - Floating Islands"),
    SKY_ISLAND_MEDIUM(41, "The End - Medium Island"),
    SKY_ISLAND_HIGH(42, "The End - High Island"),
    SKY_ISLAND_BARREN(43, "The End - Barren Island"),
    WARM_OCEAN(44, "Warm Ocean"),
    LUKEWARM_OCEAN(45, "Lukewarm Ocean"),
    COLD_OCEAN(46, "Cold Ocean"),
    WARM_DEEP_OCEAN(47, "Warm Deep Ocean"),
    LUKEWARM_DEEP_OCEAN(48, "Lukewarm Deep Ocean"),
    COLD_DEEP_OCEAN(49, "Cold Deep Ocean"),
    FROZEN_DEEP_OCEAN(50, "Frozen Deep Ocean"),
    THE_VOID(127, "The Void"),
    MUTATED_PLAINS(129, "Sunflower Plains"),
    MUTATED_DESERT(130, "Desert M"),
    MUTATED_EXTREME_HILLS(131, "Extreme Hills M"),
    MUTATED_FOREST(132, "Flower Forest"),
    MUTATED_TAIGA(133, "Taiga M"),
    MUTATED_SWAMPLAND(134, "Swampland M"),
    MUTATED_ICE_FLATS(140, "Ice Plains Spikes"),
    MUTATED_JUNGLE(149, "Jungle M"),
    MUTATED_JUNGLE_EDGE(151, "Jungle Edge M"),
    MUTATED_BIRCH_FOREST(155, "Birch Forest M"),
    MUTATED_BIRCH_FOREST_HILLS(156, "Birch Forest Hills M"),
    MUTATED_ROOFED_FOREST(157, "Roofed Forest M"),
    MUTATED_TAIGA_COLD(158, "Cold Taiga M"),
    MUTATED_REDWOOD_TAIGA(160, "Mega Spruce Taiga"),
    MUTATED_REDWOOD_TAIGA_HILLS(161, "Redwood Taiga Hills M"),
    MUTATED_EXTREME_HILLS_WITH_TREES(162, "Extreme Hills+ M"),
    MUTATED_SAVANNA(163, "Savanna M"),
    MUTATED_SAVANNA_ROCK(164, "Savanna Plateau M"),
    MUTATED_MESA(165, "Mesa (Bryce)"),
    MUTATED_MESA_ROCK(166, "Mesa Plateau F M"),
    MUTATED_MESA_CLEAR_ROCK(167, "Mesa Plateau M");

    private static final TByteObjectHashMap<NukkitBiome> BY_ID = new TByteObjectHashMap<>();

    private final byte id;
    @Getter
    private final String displayName;

    NukkitBiome(int id, String displayName) {
        this.id = (byte) id;
        this.displayName = displayName;
        add();
    }

    public static byte idFromApi(Biome biome) {
        return values()[biome.ordinal()].id;
    }

    public static NukkitBiome byId(byte id) {
        return BY_ID.containsKey(id) ? BY_ID.get(id) : PLAINS;
    }

    public static Biome byIdApi(byte id) {
        return Biome.values()[byId(id).ordinal()];
    }

    private void add() {
        BY_ID.put(id, this);
    }

    public byte id() {
        return id;
    }
}
