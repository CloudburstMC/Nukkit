package cn.nukkit.inventory;

import cn.nukkit.block.BlockIds;
import cn.nukkit.item.ItemIds;
import cn.nukkit.utils.Identifier;

import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Fuel {
    public static final Map<Identifier, Short> duration = new TreeMap<>();

    static {
        duration.put(ItemIds.COAL, (short) 1600);
        duration.put(BlockIds.COAL_BLOCK, (short) 16000);
        duration.put(BlockIds.LOG, (short) 300);
        duration.put(BlockIds.PLANKS, (short) 300);
        duration.put(BlockIds.SAPLING, (short) 100);
        duration.put(ItemIds.WOODEN_AXE, (short) 200);
        duration.put(ItemIds.WOODEN_PICKAXE, (short) 200);
        duration.put(ItemIds.WOODEN_SWORD, (short) 200);
        duration.put(ItemIds.WOODEN_SHOVEL, (short) 200);
        duration.put(ItemIds.WOODEN_HOE, (short) 200);
        duration.put(ItemIds.STICK, (short) 100);
        duration.put(BlockIds.FENCE, (short) 300);
        duration.put(BlockIds.FENCE_GATE, (short) 300);
        duration.put(BlockIds.SPRUCE_FENCE_GATE, (short) 300);
        duration.put(BlockIds.BIRCH_FENCE_GATE, (short) 300);
        duration.put(BlockIds.JUNGLE_FENCE_GATE, (short) 300);
        duration.put(BlockIds.ACACIA_FENCE_GATE, (short) 300);
        duration.put(BlockIds.DARK_OAK_FENCE_GATE, (short) 300);
        duration.put(BlockIds.OAK_STAIRS, (short) 300);
        duration.put(BlockIds.SPRUCE_STAIRS, (short) 300);
        duration.put(BlockIds.BIRCH_STAIRS, (short) 300);
        duration.put(BlockIds.JUNGLE_STAIRS, (short) 300);
        duration.put(BlockIds.TRAPDOOR, (short) 300);
        duration.put(BlockIds.CRAFTING_TABLE, (short) 300);
        duration.put(BlockIds.BOOKSHELF, (short) 300);
        duration.put(BlockIds.CHEST, (short) 300);
        duration.put(ItemIds.BUCKET, (short) 20000);
        duration.put(BlockIds.LADDER, (short) 300);
        duration.put(ItemIds.BOW, (short) 200);
        duration.put(ItemIds.BOWL, (short) 200);
        duration.put(BlockIds.LOG2, (short) 300);
        duration.put(BlockIds.DRIED_KELP_BLOCK, (short) 4000);
        duration.put(BlockIds.WOODEN_PRESSURE_PLATE, (short) 300);
        duration.put(BlockIds.ACACIA_STAIRS, (short) 300);
        duration.put(BlockIds.DARK_OAK_STAIRS, (short) 300);
        duration.put(BlockIds.TRAPPED_CHEST, (short) 300);
        duration.put(BlockIds.DAYLIGHT_DETECTOR, (short) 300);
        duration.put(BlockIds.DAYLIGHT_DETECTOR_INVERTED, (short) 300);
        duration.put(BlockIds.JUKEBOX, (short) 300);
        duration.put(BlockIds.NOTEBLOCK, (short) 300);
        duration.put(BlockIds.WOODEN_SLAB, (short) 300);
        duration.put(BlockIds.DOUBLE_WOODEN_SLAB, (short) 300);
        duration.put(ItemIds.BOAT, (short) 1200);
        duration.put(ItemIds.BLAZE_ROD, (short) 2400);
        duration.put(BlockIds.BROWN_MUSHROOM_BLOCK, (short) 300);
        duration.put(BlockIds.RED_MUSHROOM_BLOCK, (short) 300);
        duration.put(ItemIds.FISHING_ROD, (short) 300);
        duration.put(BlockIds.WOODEN_BUTTON, (short) 100);
        duration.put(ItemIds.WOODEN_DOOR, (short) 200);
        duration.put(ItemIds.SPRUCE_DOOR, (short) 200);
        duration.put(ItemIds.BIRCH_DOOR, (short) 200);
        duration.put(ItemIds.JUNGLE_DOOR, (short) 200);
        duration.put(ItemIds.ACACIA_DOOR, (short) 200);
        duration.put(ItemIds.DARK_OAK_DOOR, (short) 200);
        duration.put(ItemIds.BANNER, (short) 300);
    }
}
