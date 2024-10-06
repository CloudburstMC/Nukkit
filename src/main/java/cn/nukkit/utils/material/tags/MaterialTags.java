package cn.nukkit.utils.material.tags;

import cn.nukkit.item.RuntimeItems;
import cn.nukkit.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
public class MaterialTags {

    private static final Map<String, MaterialTag> tags = new HashMap<>();
    private static final Map<String, Set<String>> vanillaTagDefinitions = new HashMap<>();

    static {
        JsonObject json = Utils.loadJsonResource("item_tags.json").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Set<String> materials = vanillaTagDefinitions.computeIfAbsent(entry.getKey(), key -> new ObjectOpenHashSet<>());
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                String identifier = element.getAsString();
                // We use here legacy name as all items in Nukkit still use those,
                // I guess it is also better in terms of updating stuff
                String legacyName = RuntimeItems.getLegacyStringFromNew(identifier);
                if (legacyName == null) {
                    log.warn("No legacy name for " + identifier);
                } else {
                    materials.add(legacyName);
                }
            }
        }
    }

    public static final MaterialTag ARROW = register("minecraft:arrow", new LazilyInitializedMaterialTag("minecraft:arrow"));
    public static final MaterialTag BANNER = register("minecraft:banner", new LazilyInitializedMaterialTag("minecraft:banner"));
    public static final MaterialTag BOAT = register("minecraft:boat", new LazilyInitializedMaterialTag("minecraft:boat"));
    public static final MaterialTag BOATS = register("minecraft:boats", new LazilyInitializedMaterialTag("minecraft:boats"));
    public static final MaterialTag BOOKSHELF_BOOKS = register("minecraft:bookshelf_books", new LazilyInitializedMaterialTag("minecraft:bookshelf_books"));
    public static final MaterialTag CHAINMAIL_TIER = register("minecraft:chainmail_tier", new LazilyInitializedMaterialTag("minecraft:chainmail_tier"));
    public static final MaterialTag COALS = register("minecraft:coals", new LazilyInitializedMaterialTag("minecraft:coals"));
    public static final MaterialTag CRIMSON_STEMS = register("minecraft:crimson_stems", new LazilyInitializedMaterialTag("minecraft:crimson_stems"));
    public static final MaterialTag DECORATED_POT_SHERDS = register("minecraft:decorated_pot_sherds", new LazilyInitializedMaterialTag("minecraft:decorated_pot_sherds"));
    public static final MaterialTag DIAMOND_TIER = register("minecraft:diamond_tier", new LazilyInitializedMaterialTag("minecraft:diamond_tier"));
    public static final MaterialTag DIGGER = register("minecraft:digger", new LazilyInitializedMaterialTag("minecraft:digger"));
    public static final MaterialTag DOOR = register("minecraft:door", new LazilyInitializedMaterialTag("minecraft:door"));
    public static final MaterialTag GOLDEN_TIER = register("minecraft:golden_tier", new LazilyInitializedMaterialTag("minecraft:golden_tier"));
    public static final MaterialTag HANGING_ACTOR = register("minecraft:hanging_actor", new LazilyInitializedMaterialTag("minecraft:hanging_actor"));
    public static final MaterialTag HANGING_SIGN = register("minecraft:hanging_sign", new LazilyInitializedMaterialTag("minecraft:hanging_sign"));
    public static final MaterialTag HORSE_ARMOR = register("minecraft:horse_armor", new LazilyInitializedMaterialTag("minecraft:horse_armor"));
    public static final MaterialTag IRON_TIER = register("minecraft:iron_tier", new LazilyInitializedMaterialTag("minecraft:iron_tier"));
    public static final MaterialTag IS_ARMOR = register("minecraft:is_armor", new LazilyInitializedMaterialTag("minecraft:is_armor"));
    public static final MaterialTag IS_AXE = register("minecraft:is_axe", new LazilyInitializedMaterialTag("minecraft:is_axe"));
    public static final MaterialTag IS_COOKED = register("minecraft:is_cooked", new LazilyInitializedMaterialTag("minecraft:is_cooked"));
    public static final MaterialTag IS_FISH = register("minecraft:is_fish", new LazilyInitializedMaterialTag("minecraft:is_fish"));
    public static final MaterialTag IS_FOOD = register("minecraft:is_food", new LazilyInitializedMaterialTag("minecraft:is_food"));
    public static final MaterialTag IS_HOE = register("minecraft:is_hoe", new LazilyInitializedMaterialTag("minecraft:is_hoe"));
    public static final MaterialTag IS_MEAT = register("minecraft:is_meat", new LazilyInitializedMaterialTag("minecraft:is_meat"));
    public static final MaterialTag IS_MINECART = register("minecraft:is_minecart", new LazilyInitializedMaterialTag("minecraft:is_minecart"));
    public static final MaterialTag IS_PICKAXE = register("minecraft:is_pickaxe", new LazilyInitializedMaterialTag("minecraft:is_pickaxe"));
    public static final MaterialTag IS_SHOVEL = register("minecraft:is_shovel", new LazilyInitializedMaterialTag("minecraft:is_shovel"));
    public static final MaterialTag IS_SWORD = register("minecraft:is_sword", new LazilyInitializedMaterialTag("minecraft:is_sword"));
    public static final MaterialTag IS_TOOL = register("minecraft:is_tool", new LazilyInitializedMaterialTag("minecraft:is_tool"));
    public static final MaterialTag IS_TRIDENT = register("minecraft:is_trident", new LazilyInitializedMaterialTag("minecraft:is_trident"));
    public static final MaterialTag LEATHER_TIER = register("minecraft:leather_tier", new LazilyInitializedMaterialTag("minecraft:leather_tier"));
    public static final MaterialTag LECTERN_BOOKS = register("minecraft:lectern_books", new LazilyInitializedMaterialTag("minecraft:lectern_books"));
    public static final MaterialTag LOGS = register("minecraft:logs", new LazilyInitializedMaterialTag("minecraft:logs"));
    public static final MaterialTag LOGS_THAT_BURN = register("minecraft:logs_that_burn", new LazilyInitializedMaterialTag("minecraft:logs_that_burn"));
    public static final MaterialTag MANGROVE_LOGS = register("minecraft:mangrove_logs", new LazilyInitializedMaterialTag("minecraft:mangrove_logs"));
    public static final MaterialTag MUSIC_DISC = register("minecraft:music_disc", new LazilyInitializedMaterialTag("minecraft:music_disc"));
    public static final MaterialTag NETHERITE_TIER = register("minecraft:netherite_tier", new LazilyInitializedMaterialTag("minecraft:netherite_tier"));
    public static final MaterialTag PLANKS = register("minecraft:planks", new LazilyInitializedMaterialTag("minecraft:planks"));
    public static final MaterialTag SAND = register("minecraft:sand", new LazilyInitializedMaterialTag("minecraft:sand"));
    public static final MaterialTag SIGN = register("minecraft:sign", new LazilyInitializedMaterialTag("minecraft:sign"));
    public static final MaterialTag SOUL_FIRE_BASE_BLOCKS = register("minecraft:soul_fire_base_blocks", new LazilyInitializedMaterialTag("minecraft:soul_fire_base_blocks"));
    public static final MaterialTag SPAWN_EGG = register("minecraft:spawn_egg", new LazilyInitializedMaterialTag("minecraft:spawn_egg"));
    public static final MaterialTag STONE_BRICKS = register("minecraft:stone_bricks", new LazilyInitializedMaterialTag("minecraft:stone_bricks"));
    public static final MaterialTag STONE_CRAFTING_MATERIALS = register("minecraft:stone_crafting_materials", new LazilyInitializedMaterialTag("minecraft:stone_crafting_materials"));
    public static final MaterialTag STONE_TIER = register("minecraft:stone_tier", new LazilyInitializedMaterialTag("minecraft:stone_tier"));
    public static final MaterialTag STONE_TOOL_MATERIALS = register("minecraft:stone_tool_materials", new LazilyInitializedMaterialTag("minecraft:stone_tool_materials"));
    public static final MaterialTag TRANSFORM_MATERIALS = register("minecraft:transform_materials", new LazilyInitializedMaterialTag("minecraft:transform_materials"));
    public static final MaterialTag TRANSFORM_TEMPLATES = register("minecraft:transform_templates", new LazilyInitializedMaterialTag("minecraft:transform_templates"));
    public static final MaterialTag TRANSFORMABLE_ITEMS = register("minecraft:transformable_items", new LazilyInitializedMaterialTag("minecraft:transformable_items"));
    public static final MaterialTag TRIM_MATERIALS = register("minecraft:trim_materials", new LazilyInitializedMaterialTag("minecraft:trim_materials"));
    public static final MaterialTag TRIM_TEMPLATES = register("minecraft:trim_templates", new LazilyInitializedMaterialTag("minecraft:trim_templates"));
    public static final MaterialTag TRIMMABLE_ARMORS = register("minecraft:trimmable_armors", new LazilyInitializedMaterialTag("minecraft:trimmable_armors"));
    public static final MaterialTag VIBRATION_DAMPER = register("minecraft:vibration_damper", new LazilyInitializedMaterialTag("minecraft:vibration_damper"));
    public static final MaterialTag WARPED_STEMS = register("minecraft:warped_stems", new LazilyInitializedMaterialTag("minecraft:warped_stems"));
    public static final MaterialTag WOODEN_SLABS = register("minecraft:wooden_slabs", new LazilyInitializedMaterialTag("minecraft:wooden_slabs"));
    public static final MaterialTag WOODEN_TIER = register("minecraft:wooden_tier", new LazilyInitializedMaterialTag("minecraft:wooden_tier"));
    public static final MaterialTag WOOL = register("minecraft:wool", new LazilyInitializedMaterialTag("minecraft:wool"));

    public static MaterialTag register(String tagName, MaterialTag tag) {
        if (tags.containsKey(tagName)) {
            throw new IllegalArgumentException("Tag " + tagName + " is already registered");
        }
        tags.put(tagName, tag);
        return tag;
    }

    public static MaterialTag get(String tag) {
        return tags.get(tag);
    }

    protected static Set<String> getVanillaDefinitions(String tag) {
        return vanillaTagDefinitions.get(tag);
    }
}
