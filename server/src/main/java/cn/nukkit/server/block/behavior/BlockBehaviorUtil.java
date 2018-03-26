package cn.nukkit.server.block.behavior;

import cn.nukkit.api.item.ItemType;
import gnu.trove.TCollections;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import lombok.experimental.UtilityClass;

import static cn.nukkit.api.item.ItemTypes.*;

@UtilityClass
public class BlockBehaviorUtil {
    private static final TObjectFloatMap<ItemType> TOOL_EFFICIENCY;

    static {
        TObjectFloatMap<ItemType> toolEfficiency = new TObjectFloatHashMap<>(26, 0.5f, 1f);
        toolEfficiency.put(WOODEN_SWORD, 1.5f);
        toolEfficiency.put(STONE_SWORD, 1.5f);
        toolEfficiency.put(IRON_SWORD, 1.5f);
        toolEfficiency.put(DIAMOND_SWORD, 1.5f);
        toolEfficiency.put(GOLDEN_SWORD, 1.5f);

        toolEfficiency.put(WOODEN_AXE, 2f);
        toolEfficiency.put(WOODEN_HOE, 2f);
        toolEfficiency.put(WOODEN_PICKAXE, 2f);
        toolEfficiency.put(WOODEN_SHOVEL, 2f);

        toolEfficiency.put(STONE_AXE, 4f);
        toolEfficiency.put(STONE_HOE, 4f);
        toolEfficiency.put(STONE_PICKAXE, 4f);
        toolEfficiency.put(STONE_SHOVEL, 4f);

        toolEfficiency.put(IRON_AXE, 6f);
        toolEfficiency.put(IRON_HOE, 6f);
        toolEfficiency.put(IRON_PICKAXE, 6f);
        toolEfficiency.put(IRON_SHOVEL, 6f);

        toolEfficiency.put(DIAMOND_AXE, 8f);
        toolEfficiency.put(DIAMOND_HOE, 8f);
        toolEfficiency.put(DIAMOND_PICKAXE, 8f);
        toolEfficiency.put(DIAMOND_SHOVEL, 8f);

        toolEfficiency.put(GOLDEN_AXE, 12f);
        toolEfficiency.put(GOLDEN_HOE, 12f);
        toolEfficiency.put(GOLDEN_PICKAXE, 12f);
        toolEfficiency.put(GOLDEN_SHOVEL, 12f);

        toolEfficiency.put(SHEARS, 15f);

        TOOL_EFFICIENCY = TCollections.unmodifiableMap(toolEfficiency);
    }

    public static float getToolEfficiency(ItemType type) {
        return TOOL_EFFICIENCY.get(type);
    }
}
