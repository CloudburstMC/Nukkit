package cn.nukkit.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryType {
    public static final int CHEST = 0;
    public static final int DOUBLE_CHEST = 1;
    public static final int PLAYER = 2;
    public static final int FURNACE = 3;
    public static final int CRAFTING = 4;
    public static final int WORKBENCH = 5;
    public static final int STONECUTTER = 6;
    public static final int BREWING_STAND = 7;
    public static final int ANVIL = 8;
    public static final int ENCHANT_TABLE = 9;


    private static Map<Integer, InventoryType> defaults = new HashMap<>();

    private int size;
    private String title;
    private int typeId;

    public static InventoryType get(int index) {
        return defaults.containsKey(index) ? defaults.get(index) : null;
    }

    public static void init() {
        if (!defaults.isEmpty()) {
            return;
        }

        defaults.put(CHEST, new InventoryType(27, "Chest", 0));
        defaults.put(DOUBLE_CHEST, new InventoryType((27 + 27), "Double Chest", 0));
        defaults.put(PLAYER, new InventoryType(49, "Player", 0)); //36 CONTAINER, 4 ARMOR (9 reference HOTBAR slots)
        defaults.put(FURNACE, new InventoryType(3, "Furnace", 2));
        defaults.put(CRAFTING, new InventoryType(5, "Crafting", 1)); //4 CRAFTING slots, 1 RESULT
        defaults.put(WORKBENCH, new InventoryType(10, "Crafting", 1)); //9 CRAFTING slots, 1 RESULT
        defaults.put(STONECUTTER, new InventoryType(10, "Crafting", 1)); //9 CRAFTING slots, 1 RESULT
        defaults.put(ENCHANT_TABLE, new InventoryType(2, "Enchant", 4)); //1 INPUT/OUTPUT, 1 LAPIS
        defaults.put(BREWING_STAND, new InventoryType(4, "Brewing", 5)); //1 INPUT, 3 POTION
        defaults.put(ANVIL, new InventoryType(3, "Anvil", 6)); //2 INPUT, 1 OUTPUT
    }

    public InventoryType(int defaultSize, String defaultTile, int typeId) {
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

    public int getNetworkType() {
        return typeId;
    }
}
