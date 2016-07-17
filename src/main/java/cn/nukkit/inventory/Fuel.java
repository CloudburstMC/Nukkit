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
    }
}
