package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Fuel {
    public static final Map<Integer, Short> duration = new TreeMap<>();

    static {
        addItem(Item.COAL, (short) 1600);
        addBlock(Item.COAL_BLOCK, (short) 16000);
        addItem(Item.TRUNK, (short) 300);
        addItem(Item.WOODEN_PLANKS, (short) 300);
        addItem(Item.SAPLING, (short) 100);
        addItem(Item.WOODEN_AXE, (short) 200);
        addItem(Item.WOODEN_PICKAXE, (short) 200);
        addItem(Item.WOODEN_SWORD, (short) 200);
        addItem(Item.WOODEN_SHOVEL, (short) 200);
        addItem(Item.WOODEN_HOE, (short) 200);
        addItem(Item.STICK, (short) 100);
        addItem(Item.FENCE, (short) 300);
        addItem(Item.FENCE_GATE, (short) 300);
        addItem(Item.FENCE_GATE_SPRUCE, (short) 300);
        addItem(Item.FENCE_GATE_BIRCH, (short) 300);
        addItem(Item.FENCE_GATE_JUNGLE, (short) 300);
        addItem(Item.FENCE_GATE_ACACIA, (short) 300);
        addItem(Item.FENCE_GATE_DARK_OAK, (short) 300);
        addItem(Item.WOODEN_STAIRS, (short) 300);
        addItem(Item.SPRUCE_WOOD_STAIRS, (short) 300);
        addItem(Item.BIRCH_WOOD_STAIRS, (short) 300);
        addItem(Item.JUNGLE_WOOD_STAIRS, (short) 300);
        addItem(Item.TRAPDOOR, (short) 300);
        addItem(Item.WORKBENCH, (short) 300);
        addItem(Item.BOOKSHELF, (short) 300);
        addItem(Item.CHEST, (short) 300);
        addItem(Item.BUCKET, (short) 20000);
        addItem(Item.LADDER, (short) 300);
        addItem(Item.BOW, (short) 200);
        addItem(Item.BOWL, (short) 200);
        addItem(Item.WOOD2, (short) 300);
        addItem(Item.WOODEN_PRESSURE_PLATE, (short) 300);
        addItem(Item.ACACIA_WOOD_STAIRS, (short) 300);
        addItem(Item.DARK_OAK_WOOD_STAIRS, (short) 300);
        addItem(Item.TRAPPED_CHEST, (short) 300);
        addItem(Item.DAYLIGHT_DETECTOR, (short) 300);
        addItem(Item.DAYLIGHT_DETECTOR_INVERTED, (short) 300);
        addItem(Item.JUKEBOX, (short) 300);
        addBlock(Item.NOTEBLOCK, (short) 300);
        addItem(Item.WOOD_SLAB, (short) 300);
        addItem(Item.DOUBLE_WOOD_SLAB, (short) 300);
        addItem(Item.BOAT, (short) 1200);
        addItem(Item.BLAZE_ROD, (short) 2400);
        addBlock(Item.BROWN_MUSHROOM_BLOCK, (short) 300);
        addBlock(Item.RED_MUSHROOM_BLOCK, (short) 300);
        addItem(Item.FISHING_ROD, (short) 300);
        addItem(Item.WOODEN_BUTTON, (short) 100);
        addItem(Item.WOODEN_DOOR, (short) 200);
        addItem(Item.SPRUCE_DOOR, (short) 200);
        addItem(Item.BIRCH_DOOR, (short) 200);
        addItem(Item.JUNGLE_DOOR, (short) 200);
        addItem(Item.ACACIA_DOOR, (short) 200);
        addItem(Item.DARK_OAK_DOOR, (short) 200);
        addItem(Item.BANNER, (short) 300);
        addBlock(Item.DRIED_KELP_BLOCK, (short) 4000);
    }

    private static void addItem(int itemID, short fuelDuration) {
        duration.put(itemID, fuelDuration);
    }

    private static void addBlock(int blockID, short fuelDuration) {
        duration.put(blockID > 255 ? 255 - blockID : blockID, fuelDuration); // ItemBlock have a negative ID
    }
}
