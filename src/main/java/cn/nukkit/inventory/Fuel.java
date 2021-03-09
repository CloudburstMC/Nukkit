package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Fuel {
    public static final Map<Integer, Short> duration = new TreeMap<>();

    static {
        duration.put(Item.COAL, (short) 1600);
        duration.put(Item.COAL_BLOCK, (short) 16000);
        duration.put(Item.TRUNK, (short) 300);
        duration.put(Item.WOODEN_PLANKS, (short) 300);
        duration.put(Item.SAPLING, (short) 100);
        duration.put(Item.WOODEN_AXE, (short) 200);
        duration.put(Item.WOODEN_PICKAXE, (short) 200);
        duration.put(Item.WOODEN_SWORD, (short) 200);
        duration.put(Item.WOODEN_SHOVEL, (short) 200);
        duration.put(Item.WOODEN_HOE, (short) 200);
        duration.put(Item.STICK, (short) 100);
        duration.put(Item.FENCE, (short) 300);
        duration.put(Item.FENCE_GATE, (short) 300);
        duration.put(Item.FENCE_GATE_SPRUCE, (short) 300);
        duration.put(Item.FENCE_GATE_BIRCH, (short) 300);
        duration.put(Item.FENCE_GATE_JUNGLE, (short) 300);
        duration.put(Item.FENCE_GATE_ACACIA, (short) 300);
        duration.put(Item.FENCE_GATE_DARK_OAK, (short) 300);
        duration.put(Item.WOODEN_STAIRS, (short) 300);
        duration.put(Item.SPRUCE_WOOD_STAIRS, (short) 300);
        duration.put(Item.BIRCH_WOOD_STAIRS, (short) 300);
        duration.put(Item.JUNGLE_WOOD_STAIRS, (short) 300);
        duration.put(Item.TRAPDOOR, (short) 300);
        duration.put(Item.WORKBENCH, (short) 300);
        duration.put(Item.BOOKSHELF, (short) 300);
        duration.put(Item.CHEST, (short) 300);
        duration.put(Item.BUCKET, (short) 20000);
        duration.put(Item.LADDER, (short) 300);
        duration.put(Item.BOW, (short) 200);
        duration.put(Item.BOWL, (short) 200);
        duration.put(Item.WOOD2, (short) 300);
        duration.put(Item.WOODEN_PRESSURE_PLATE, (short) 300);
        duration.put(Item.ACACIA_WOOD_STAIRS, (short) 300);
        duration.put(Item.DARK_OAK_WOOD_STAIRS, (short) 300);
        duration.put(Item.TRAPPED_CHEST, (short) 300);
        duration.put(Item.DAYLIGHT_DETECTOR, (short) 300);
        duration.put(Item.DAYLIGHT_DETECTOR_INVERTED, (short) 300);
        duration.put(Item.JUKEBOX, (short) 300);
        duration.put(Item.NOTEBLOCK, (short) 300);
        duration.put(Item.WOOD_SLAB, (short) 300);
        duration.put(Item.DOUBLE_WOOD_SLAB, (short) 300);
        duration.put(Item.BOAT, (short) 1200);
        duration.put(Item.BLAZE_ROD, (short) 2400);
        duration.put(Item.BROWN_MUSHROOM_BLOCK, (short) 300);
        duration.put(Item.RED_MUSHROOM_BLOCK, (short) 300);
        duration.put(Item.FISHING_ROD, (short) 300);
        duration.put(Item.WOODEN_BUTTON, (short) 300);
        duration.put(Item.WOODEN_DOOR, (short) 200);
        duration.put(Item.SPRUCE_DOOR, (short) 200);
        duration.put(Item.BIRCH_DOOR, (short) 200);
        duration.put(Item.JUNGLE_DOOR, (short) 200);
        duration.put(Item.ACACIA_DOOR, (short) 200);
        duration.put(Item.DARK_OAK_DOOR, (short) 200);
        duration.put(Item.BANNER, (short) 300);
        duration.put(Item.DEAD_BUSH, (short) 100);
        duration.put(Item.SIGN, (short) 200);
    }
}
