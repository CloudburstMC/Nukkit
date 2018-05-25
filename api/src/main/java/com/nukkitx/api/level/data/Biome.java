package com.nukkitx.api.level.data;

import gnu.trove.map.hash.TByteObjectHashMap;

public enum Biome {
    OCEAN(0),
    PLAINS(1),
    DESERT(2),
    EXTREME_HILLS(3),
    FOREST(4),
    TAIGA(5),
    SWAMPLAND(6),
    RIVER(7),
    HELL(8),
    SKY(9),
    FROZEN_OCEAN(10),
    FROZEN_RIVER(11),
    ICE_FLATS(12),
    ICE_MOUNTAINS(13),
    MUSHROOM_ISLAND(14),
    MUSHROOM_ISLAND_SHORE(15),
    BEACHES(16),
    DESERT_HILLS(17),
    FOREST_HILLS(18),
    TAIGA_HILLS(19),
    SMALLER_EXTREME_HILLS(20),
    JUNGLE(21),
    JUNGLE_HILLS(22),
    JUNGLE_EDGE(23),
    DEEP_OCEAN(24),
    STONE_BEACH(25),
    COLD_BEACH(26),
    BIRCH_FOREST(27),
    BIRCH_FOREST_HILLS(28),
    ROOFED_FOREST(29),
    TAIGA_COLD(30),
    TAIGA_COLD_HILLS(31),
    REDWOOD_TAIGA(32),
    REDWOOD_TAIGA_HILLS(33),
    EXTREME_HILLS_WITH_TREES(34),
    SAVANNA(35),
    SAVANNA_ROCK(36),
    MESA(37),
    MESA_ROCK(38),
    MESA_CLEAR_ROCK(39),
    SKY_ISLAND_LOW(40),
    SKY_ISLAND_MEDIUM(41),
    SKY_ISLAND_HIGH(42),
    SKY_ISLAND_BARREN(43),
    WARM_OCEAN(44),
    LUKEWARM_OCEAN(45),
    COLD_OCEAN(46),
    WARM_DEEP_OCEAN(47),
    LUKEWARM_DEEP_OCEAN(48),
    COLD_DEEP_OCEAN(49),
    FROZEN_DEEP_OCEAN(50),
    THE_VOID(127),
    MUTATED_PLAINS(129),
    MUTATED_DESERT(130),
    MUTATED_EXTREME_HILLS(131),
    MUTATED_FOREST(132),
    MUTATED_TAIGA(133),
    MUTATED_SWAMPLAND(134),
    MUTATED_ICE_FLATS(140),
    MUTATED_JUNGLE(149),
    MUTATED_JUNGLE_EDGE(151),
    MUTATED_BIRCH_FOREST(155),
    MUTATED_BIRCH_FOREST_HILLS(156),
    MUTATED_ROOFED_FOREST(157),
    MUTATED_TAIGA_COLD(158),
    MUTATED_REDWOOD_TAIGA(160),
    MUTATED_REDWOOD_TAIGA_HILLS(161),
    MUTATED_EXTREME_HILLS_WITH_TREES(162),
    MUTATED_SAVANNA(163),
    MUTATED_SAVANNA_ROCK(164),
    MUTATED_MESA(165),
    MUTATED_MESA_ROCK(166),
    MUTATED_MESA_CLEAR_ROCK(167);

    private static TByteObjectHashMap<Biome> BY_ID;

    private final byte id;

    Biome(int id) {
        this.id = (byte) id;
        add();
    }

    public static Biome byId(byte id) {
        return BY_ID.containsKey(id) ? BY_ID.get(id) : PLAINS;
    }

    private synchronized void add() {
        if (BY_ID == null) {
            BY_ID = new TByteObjectHashMap<>();
        }

        BY_ID.put(id, this);
    }

    public byte id() {
        return id;
    }
}
