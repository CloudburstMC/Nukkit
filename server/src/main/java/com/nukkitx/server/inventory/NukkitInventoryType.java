package com.nukkitx.server.inventory;

import com.nukkitx.api.inventory.InventoryType;

public enum NukkitInventoryType {
    CHEST(27, 0, 10),
    ENDER_CHEST(27, 0, 10),
    DOUBLE_CHEST(27 + 27, 0, 10),
    PLAYER(36, -1, 0),
    FURNACE(3, 2, 11),
    CRAFTING(5, 1),
    WORKBENCH(10, 1),
    BREWING_STAND(5, 4),
    ANVIL(3, 5),
    ENCHANTMENT_TABLE(2, 4, 3),
    DISPENSER(9, 6),
    DROPPER(9, 7),
    HOPPER(5, 8),
    CURSOR(1, -1);

    private static final InventoryType[] API_VALUES = InventoryType.values();
    private static final NukkitInventoryType[] VALUES = values();

    private final int size;
    private final byte type;
    private final byte id;

    NukkitInventoryType(int size, int type) {
        this(size, type, -1);
    }

    NukkitInventoryType(int size, int type, int id) {
        this.size = size;
        this.type = (byte) type;
        this.id = (byte) id;
    }

    public int getSize() {
        return size;
    }

    public byte getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public static NukkitInventoryType fromApi(InventoryType type) {
        return VALUES[type.ordinal()];
    }

    public InventoryType toApi() {
        return API_VALUES[ordinal()];
    }
}
