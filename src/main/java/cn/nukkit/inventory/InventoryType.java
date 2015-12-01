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
    public static final byte BREWING_STAND = 7;
    public static final byte ANVIL = 8;
    public static final byte ENCHANT_TABLE = 9;


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
        defaults.put(PLAYER, new InventoryType((byte) 49, "Player", (byte) 0)); //36 CONTAINER, 4 ARMOR (9 reference HOTBAR slots)
        defaults.put(FURNACE, new InventoryType((byte) 3, "Furnace", (byte) 2));
        defaults.put(CRAFTING, new InventoryType((byte) 5, "Crafting", (byte) 1)); //4 CRAFTING slots, 1 RESULT
        defaults.put(WORKBENCH, new InventoryType((byte) 10, "Crafting", (byte) 1)); //9 CRAFTING slots, 1 RESULT
        defaults.put(STONECUTTER, new InventoryType((byte) 10, "Crafting", (byte) 1)); //9 CRAFTING slots, 1 RESULT
        defaults.put(ENCHANT_TABLE, new InventoryType((byte) 2, "Enchant", (byte) 4)); //1 INPUT/OUTPUT, 1 LAPIS
        defaults.put(BREWING_STAND, new InventoryType((byte) 4, "Brewing", (byte) 5)); //1 INPUT, 3 POTION
        defaults.put(ANVIL, new InventoryType((byte) 3, "Anvil", (byte) 6)); //2 INPUT, 1 OUTPUT
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
