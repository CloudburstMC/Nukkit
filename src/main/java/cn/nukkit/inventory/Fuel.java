package cn.nukkit.inventory;

import cn.nukkit.block.BlockID;
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
        addItem(ItemID.COAL, (short) 1600);
        addBlock(BlockID.COAL_BLOCK, (short) 16000);
        addBlock(BlockID.TRUNK, (short) 300);
        addBlock(BlockID.WOODEN_PLANKS, (short) 300);
        addBlock(BlockID.SAPLING, (short) 100);
        addItem(ItemID.WOODEN_AXE, (short) 200);
        addItem(ItemID.WOODEN_PICKAXE, (short) 200);
        addItem(ItemID.WOODEN_SWORD, (short) 200);
        addItem(ItemID.WOODEN_SHOVEL, (short) 200);
        addItem(ItemID.WOODEN_HOE, (short) 200);
        addItem(ItemID.STICK, (short) 100);
        addBlock(BlockID.FENCE, (short) 300);
        addBlock(BlockID.FENCE_GATE, (short) 300);
        addBlock(BlockID.FENCE_GATE_SPRUCE, (short) 300);
        addBlock(BlockID.FENCE_GATE_BIRCH, (short) 300);
        addBlock(BlockID.FENCE_GATE_JUNGLE, (short) 300);
        addBlock(BlockID.FENCE_GATE_ACACIA, (short) 300);
        addBlock(BlockID.FENCE_GATE_DARK_OAK, (short) 300);
        addBlock(BlockID.WOODEN_STAIRS, (short) 300);
        addBlock(BlockID.SPRUCE_WOOD_STAIRS, (short) 300);
        addBlock(BlockID.BIRCH_WOOD_STAIRS, (short) 300);
        addBlock(BlockID.JUNGLE_WOOD_STAIRS, (short) 300);
        addBlock(BlockID.TRAPDOOR, (short) 300);
        addBlock(BlockID.WORKBENCH, (short) 300);
        addBlock(BlockID.BOOKSHELF, (short) 300);
        addBlock(BlockID.CHEST, (short) 300);
        addItem(ItemID.BUCKET, (short) 20000);
        addBlock(BlockID.LADDER, (short) 300);
        addItem(ItemID.BOW, (short) 200);
        addItem(ItemID.BOWL, (short) 200);
        addBlock(BlockID.WOOD2, (short) 300);
        addBlock(BlockID.WOODEN_PRESSURE_PLATE, (short) 300);
        addBlock(BlockID.ACACIA_WOOD_STAIRS, (short) 300);
        addBlock(BlockID.DARK_OAK_WOOD_STAIRS, (short) 300);
        addBlock(BlockID.TRAPPED_CHEST, (short) 300);
        addBlock(BlockID.DAYLIGHT_DETECTOR, (short) 300);
        addBlock(BlockID.DAYLIGHT_DETECTOR_INVERTED, (short) 300);
        addBlock(BlockID.JUKEBOX, (short) 300);
        addBlock(BlockID.NOTEBLOCK, (short) 300);
        addBlock(BlockID.WOOD_SLAB, (short) 300);
        addBlock(BlockID.DOUBLE_WOOD_SLAB, (short) 300);
        addItem(ItemID.BOAT, (short) 1200);
        addItem(ItemID.BLAZE_ROD, (short) 2400);
        addBlock(BlockID.BROWN_MUSHROOM_BLOCK, (short) 300);
        addBlock(BlockID.RED_MUSHROOM_BLOCK, (short) 300);
        addItem(ItemID.FISHING_ROD, (short) 300);
        addBlock(BlockID.WOODEN_BUTTON, (short) 100);
        addItem(ItemID.WOODEN_DOOR, (short) 200);
        addItem(ItemID.SPRUCE_DOOR, (short) 200);
        addItem(ItemID.BIRCH_DOOR, (short) 200);
        addItem(ItemID.JUNGLE_DOOR, (short) 200);
        addItem(ItemID.ACACIA_DOOR, (short) 200);
        addItem(ItemID.DARK_OAK_DOOR, (short) 200);
        addItem(ItemID.BANNER, (short) 300);
        addBlock(BlockID.DRIED_KELP_BLOCK, (short) 4000);
    }

    private static void addItem(int itemID, short fuelDuration) {
        duration.put(itemID, fuelDuration);
    }

    private static void addBlock(int blockID, short fuelDuration) {
        duration.put(blockID > 255 ? 255 - blockID : blockID, fuelDuration); // ItemBlock have a negative ID
    }
}
