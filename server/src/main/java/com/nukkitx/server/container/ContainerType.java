package com.nukkitx.server.container;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public enum ContainerType {
    UNTRACKED_UI_INTERACTION(-9),
    INVENTORY(-1),
    CONTAINER(0),
    WORKBENCH(1),
    FURNACE(2),
    ENCHANTMENT(3),
    BREWING_STAND(4),
    ANVIL(5),
    DISPENSER(6),
    DROPPER(7),
    HOPPER(8),
    CAULDRON(9),
    CHEST(10),
    MINECART_HOPPER(11),
    HORSE(12),
    BEACON(13),
    STRUCTURE_EDITOR(14),
    TRADING(15),
    COMMAND_BLOCK(16),
    JUKEBOX(17),
    COMPOUND_CREATOR(20),
    ELEMENT_CONSTRUCTOR(21),
    MATERIAL_REDUCER(22),
    LAB_TABLE(23);

    public static final TIntObjectMap<ContainerType> BY_ID = new TIntObjectHashMap<>();

    static {
        for (ContainerType type : values()) {
            BY_ID.put(type.id, type);
        }
    }

    private final int id;

    ContainerType(final int id) {
        this.id = id;
    }

    public static ContainerType fromId(int id) {
        return BY_ID.get(id);
    }

    public int id() {
        return id;
    }
}
