package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Fuel {
    public static Map<Integer, Integer> duration = new TreeMap<>();

    static {
        duration.put(Item.COAL, 1600);
        duration.put(Item.COAL_BLOCK, 16000);
        duration.put(Item.TRUNK, 300);
        duration.put(Item.WOODEN_PLANKS, 300);
        duration.put(Item.SAPLING, 100);
        duration.put(Item.WOODEN_AXE, 200);
        duration.put(Item.WOODEN_PICKAXE, 200);
        duration.put(Item.WOODEN_SWORD, 200);
        duration.put(Item.WOODEN_SHOVEL, 200);
        duration.put(Item.WOODEN_HOE, 200);
        duration.put(Item.STICK, 100);
        duration.put(Item.FENCE, 300);
        duration.put(Item.FENCE_GATE, 300);
        duration.put(Item.FENCE_GATE_SPRUCE, 300);
        duration.put(Item.FENCE_GATE_BIRCH, 300);
        duration.put(Item.FENCE_GATE_JUNGLE, 300);
        duration.put(Item.FENCE_GATE_ACACIA, 300);
        duration.put(Item.FENCE_GATE_DARK_OAK, 300);
        duration.put(Item.WOODEN_STAIRS, 300);
        duration.put(Item.SPRUCE_WOOD_STAIRS, 300);
        duration.put(Item.BIRCH_WOOD_STAIRS, 300);
        duration.put(Item.JUNGLE_WOOD_STAIRS, 300);
        duration.put(Item.TRAPDOOR, 300);
        duration.put(Item.WORKBENCH, 300);
        duration.put(Item.BOOKSHELF, 300);
        duration.put(Item.CHEST, 300);
        duration.put(Item.BUCKET, 20000);
    }
}
