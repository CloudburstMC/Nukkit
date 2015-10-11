package cn.nukkit.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryType {
    public static final byte CHEST = 0;
    public static final byte DOUBLE_CHEST = 1;
    public static final byte PLAYER = 2;
    public static final byte FURNACE = 3;
    public static final byte CRAFTING = 4;
    public static final byte WORKBENCH = 5;
    public static final byte STONECUTTER = 6;

    private static Map<Byte, InventoryType> defaults = new HashMap<>();

    private int size;
    private String title;
    private byte typeId;

    public static InventoryType get(byte index) {
        return defaults.containsKey(index) ? defaults.get(index) : null;
    }

    public static void init() {
        if (!defaults.isEmpty()) {
            return;
        }

        defaults.put(CHEST, new InventoryType((byte) 27, "Chest", (byte) 0));
        defaults.put(DOUBLE_CHEST, new InventoryType((byte) (27 + 27), "Double Chest", (byte) 0));
        defaults.put(PLAYER, new InventoryType((byte) 40, "Player", (byte) 0)); //27 CONTAINER, 4 ARMOR (9 reference HOTBAR slots)
        defaults.put(FURNACE, new InventoryType((byte) 3, "Furnace", (byte) 2));
        defaults.put(CRAFTING, new InventoryType((byte) 5, "Crafting", (byte) 1)); //4 CRAFTING slots, 1 RESULT
        defaults.put(WORKBENCH, new InventoryType((byte) 10, "Crafting", (byte) 1)); //9 CRAFTING slots, 1 RESULT
        defaults.put(STONECUTTER, new InventoryType((byte) 10, "Crafting", (byte) 3)); //9 CRAFTING slots, 1 RESULT
    }

    public InventoryType(byte defaultSize, String defaultTile, byte typeId) {
        this.size = defaultSize;
        this.title = defaultTile;
        this.typeId = typeId;
    }

    public int getDefaultSize() {
        return size;
    }

    public String getDefaultTitle() {
        return title;
    }

    public byte getNetworkType() {
        return typeId;
    }
}
