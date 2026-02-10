package cn.nukkit.inventory;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.*;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.utils.*;
import cn.nukkit.utils.material.tags.MaterialTags;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.*;
import java.util.zip.Deflater;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class CraftingManager {

    private static BatchPacket packet;
    /* Keep these public for backwards compatibility */

    @Getter
    public final Collection<Recipe> recipes = new ArrayDeque<>();
    @Getter
    protected final Map<Integer, Map<UUID, ShapedRecipe>> shapedRecipes = new Int2ObjectOpenHashMap<>();
    @Getter
    protected final Map<Integer, Map<UUID, ShapelessRecipe>> shapelessRecipes = new Int2ObjectOpenHashMap<>();
    @Getter
    public final Map<Integer, ContainerRecipe> containerRecipes = new Int2ObjectOpenHashMap<>();
    @Getter
    public final Map<UUID, MultiRecipe> multiRecipes = new HashMap<>();
    @Getter
    public final Map<Integer, FurnaceRecipe> furnaceRecipes = new Int2ObjectOpenHashMap<>();
    @Getter
    public final Map<Integer, BrewingRecipe> brewingRecipes = new Int2ObjectOpenHashMap<>();
    @Getter
    private final Map<Integer, CampfireRecipe> campfireRecipes = new Int2ObjectOpenHashMap<>();
    @Getter
    private final Map<UUID, SmithingRecipe> smithingRecipes = new HashMap<>();

    private static int RECIPE_COUNT;
    static int NEXT_NETWORK_ID = 1; // Reserve 1 for smithing_armor_trim

    public static final Comparator<Item> recipeComparator = (i1, i2) -> {
        if (i1.getId() > i2.getId()) {
            return 1;
        } else if (i1.getId() < i2.getId()) {
            return -1;
        } else if (i1.getDamage() > i2.getDamage()) {
            return 1;
        } else if (i1.getDamage() < i2.getDamage()) {
            return -1;
        } else {
            return Integer.compare(i1.getCount(), i2.getCount());
        }
    };

    @SuppressWarnings("unchecked")
    public CraftingManager() {
        MainLogger.getLogger().debug("Loading recipes...");

        Map<String, Object> root = new Config(Config.YAML).loadFromStream(Server.class.getClassLoader().getResourceAsStream("recipes.json")).getRootSection();

        RuntimeItemMapping itemMapping = RuntimeItems.getMapping();

        for (Map recipe : (List<Map>) root.get("recipes")) {
            try {
                switch (Utils.toInt(recipe.get("type"))) {
                    case 0: // shapeless
                        if (!"crafting_table".equals(recipe.get("block"))) {
                            continue;
                        }

                        Map shapelessOutput = (Map) ((List) recipe.get("output")).get(0);
                        RuntimeItemMapping.LegacyEntry shapelessOutputEntry = itemMapping.fromRuntime((int) shapelessOutput.get("legacyId"));
                        top:
                        if (shapelessOutputEntry != null && shapelessOutputEntry.getLegacyId() != 0) {
                            int outputDamage = (int) shapelessOutput.getOrDefault("damage", 0);
                            if (outputDamage == 0) {
                                outputDamage = shapelessOutputEntry.getDamage();
                            }
                            String nbt = (String) shapelessOutput.get("nbt_b64");
                            byte[] nbtBytes = new byte[0];
                            if (nbt != null) {
                                nbtBytes = Base64.getDecoder().decode(nbt);
                            }
                            Item outputItem = Item.get(shapelessOutputEntry.getLegacyId(), outputDamage, (Integer) shapelessOutput.getOrDefault("count", 1), nbtBytes);
                            List<Map> input = ((List<Map>) recipe.get("input"));
                            List<Item> sorted = new ArrayList<>();

                            for (Map<String, Object> ingredient : input) {
                                String type = (String) ingredient.get("type");
                                if (!"default".equals(type)) {
                                    if ("item_tag".equals(type)) {
                                        buildShapelessRecipeItemTagOverrides(itemMapping, input, outputItem, (String) ingredient.get("itemTag"), null, null);
                                    } else if (Nukkit.DEBUG > 1) {
                                        MainLogger.getLogger().debug("Unknown shapeless ingredient type: " + recipe);
                                    }
                                    break top;
                                }
                                Item inputItem;
                                RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromRuntime((int) ingredient.get("itemId"));
                                if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                                    if (Nukkit.DEBUG > 1) {
                                        MainLogger.getLogger().debug("Unknown shapeless input: " + recipe);
                                    }
                                    break top;
                                }
                                int aux = (int) ingredient.getOrDefault("auxValue", 0);
                                if (aux == 32767) {
                                    aux = legacyEntry.isHasDamage() ? legacyEntry.getDamage() : -1;
                                } else if (aux == 0) {
                                    aux = legacyEntry.getDamage();
                                }
                                inputItem = Item.get(legacyEntry.getLegacyId(), aux, (Integer) ingredient.getOrDefault("count", 1));
                                sorted.add(inputItem);
                            }

                            sorted.sort(recipeComparator);

                            int priority = (int) recipe.getOrDefault("priority", 0);
                            this.registerRecipe(new ShapelessRecipe((String) recipe.get("id"), priority, outputItem, sorted));

                            // Inject recipes for flight duration 2 and 3 fireworks
                            if (outputItem.getId() == Item.FIREWORKS && outputItem.getCount() == 3) {
                                sorted.add(Item.get(Item.GUNPOWDER, 0, 1));
                                sorted.sort(recipeComparator);
                                ((ItemFirework) outputItem).setFlight(2);
                                this.registerRecipe(new ShapelessRecipe(null, 0, outputItem, sorted));

                                sorted.add(Item.get(Item.GUNPOWDER, 0, 1)); // Re-using the list so only need to add one
                                sorted.sort(recipeComparator);
                                ((ItemFirework) outputItem).setFlight(3);
                                this.registerRecipe(new ShapelessRecipe(null, 0, outputItem, sorted));
                            }
                        } else {
                            if (Nukkit.DEBUG > 1) {
                                MainLogger.getLogger().debug("Unknown shapeless output: " + recipe);
                            }
                        }
                        break;
                    case 1: // shaped
                        if (!"crafting_table".equals(recipe.get("block"))) {
                            continue;
                        }

                        Map shapedOutput = (Map) ((List) recipe.get("output")).get(0);
                        RuntimeItemMapping.LegacyEntry shapedOutputEntry = itemMapping.fromRuntime((int) shapedOutput.get("legacyId"));
                        top:
                        if (shapedOutputEntry != null && shapedOutputEntry.getLegacyId() != 0) {
                            int outputDamage = (int) shapedOutput.getOrDefault("damage", 0);
                            if (outputDamage == 0) {
                                outputDamage = shapedOutputEntry.getDamage();
                            }
                            String nbt = (String) shapedOutput.get("nbt_b64");
                            byte[] nbtBytes = new byte[0];
                            if (nbt != null) {
                                nbtBytes = Base64.getDecoder().decode(nbt);
                            }
                            Item outputItem = Item.get(shapedOutputEntry.getLegacyId(), outputDamage, (Integer) shapedOutput.getOrDefault("count", 1), nbtBytes);
                            String[] shape = ((List<String>) recipe.get("shape")).toArray(new String[0]);
                            Map<Character, Item> ingredients = new CharObjectHashMap<>();
                            Map<String, Map<String, Object>> input = (Map) recipe.get("input");

                            for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
                                Item inputItem = null;
                                String type = (String) ingredientEntry.getValue().get("type");
                                if (!"default".equals(type)) {
                                    if ("item_tag".equals(type)) {
                                        buildShapedRecipeItemTagOverrides(itemMapping, input, shape, outputItem, (String) ingredientEntry.getValue().get("itemTag"), null, null);
                                    } else if ("complex_alias".equals(type)) {
                                        // TODO: Variants. Maybe we could run this through buildShapedRecipeItemTagOverrides as a fake itemTag?
                                        // Currently handled by needExpandLegacy:
                                        // minecraft:Torch_recipeId
                                        // minecraft:smoker_from_log2
                                        // all campfires
                                        // soul torch
                                        switch ((String) recipe.get("id")) {
                                            case "minecraft:painting":
                                                inputItem = Item.get(BlockID.WOOL, 0, 1);
                                                break;
                                            case "minecraft:purpur_stairs":
                                                inputItem = Item.get(BlockID.PURPUR_BLOCK, 0, 1);
                                                break;
                                            case "minecraft:stonecutter":
                                                inputItem = Item.get(BlockID.STONE, 0, 1);
                                                break;
                                            case "minecraft:tnt":
                                                inputItem = Item.get(BlockID.SAND, 0, 1);
                                                break;
                                        }
                                        if (Nukkit.DEBUG > 1 && inputItem == null) {
                                            MainLogger.getLogger().debug("Missing shaped ingredient complex_alias: " + recipe);
                                        }
                                    } else if (Nukkit.DEBUG > 1) {
                                        MainLogger.getLogger().debug("Unknown shaped ingredient type: " + recipe);
                                    }
                                    if (inputItem == null) {
                                        break top;
                                    }
                                }
                                if (inputItem == null) {
                                    RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromRuntime((int) ingredientEntry.getValue().get("itemId"));
                                    if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                                        if (Nukkit.DEBUG > 1) {
                                            MainLogger.getLogger().debug("Unknown shaped input: " + recipe);
                                        }
                                        break top;
                                    }
                                    int aux = (int) ingredientEntry.getValue().getOrDefault("auxValue", 0);
                                    if (aux == 32767) {
                                        aux = legacyEntry.isHasDamage() ? legacyEntry.getDamage() : -1;
                                    } else if (aux == 0) {
                                        aux = legacyEntry.getDamage();
                                    }
                                    inputItem = Item.get(legacyEntry.getLegacyId(), aux, (Integer) ingredientEntry.getValue().getOrDefault("count", 1));
                                }
                                ingredients.put(ingredientEntry.getKey().charAt(0), inputItem);
                            }

                            int priority = (int) recipe.getOrDefault("priority", 0);
                            this.registerRecipe(new ShapedRecipe((String) recipe.get("id"), priority, outputItem, shape, ingredients, Collections.EMPTY_LIST));
                        } else {
                            if (Nukkit.DEBUG > 1) {
                                MainLogger.getLogger().debug("Unknown shaped output: " + recipe);
                            }
                        }
                        break;
                    case 3: // smelting
                        String smeltingBlock = (String) recipe.get("block");
                        if (!"furnace".equals(smeltingBlock) && !"campfire".equals(smeltingBlock)) {
                            continue;
                        }

                        Map input = (Map) recipe.get("input");
                        Map output = (Map) recipe.get("output");
                        RuntimeItemMapping.LegacyEntry furnaceInputEntry = itemMapping.fromIdentifier((String) input.get("id"));
                        RuntimeItemMapping.LegacyEntry furnaceOutputEntry = itemMapping.fromIdentifier((String) output.get("id"));

                        if (furnaceInputEntry != null && furnaceOutputEntry != null && furnaceInputEntry.getLegacyId() != 0 && furnaceOutputEntry.getLegacyId() != 0) {
                            Item inputItem = Item.get(furnaceInputEntry.getLegacyId(), furnaceInputEntry.getDamage(), (Integer) input.getOrDefault("count", 1));
                            Item outputItem = Item.get(furnaceOutputEntry.getLegacyId(), furnaceOutputEntry.getDamage(), (Integer) output.getOrDefault("count", 1));

                            switch (smeltingBlock) {
                                case "furnace":
                                    this.registerRecipe(new FurnaceRecipe(outputItem, inputItem));
                                    break;
                                case "campfire":
                                    this.registerRecipe(new CampfireRecipe(outputItem, inputItem));
                                    break;
                            }
                        } else {
                            if (Nukkit.DEBUG > 1) {
                                MainLogger.getLogger().debug("Unknown smelting recipe: " + recipe);
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error while loading recipes", e);
            }
        }

        for (Map potionMix : (List<Map>) root.get("potionMixes")) {
            RuntimeItemMapping.LegacyEntry legacyEntry;

            legacyEntry = itemMapping.fromIdentifier(((String) potionMix.get("inputId")));
            if (legacyEntry == null) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown inputId: " + potionMix);
                }
                continue;
            }
            int fromPotionId = legacyEntry.getLegacyId();
            int fromPotionMeta = ((Integer) potionMix.get("inputMeta"));
            legacyEntry = itemMapping.fromIdentifier(((String) potionMix.get("reagentId")));
            if (legacyEntry == null) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown reagentId: " + potionMix);
                }
                continue;
            }
            int ingredient = legacyEntry.getLegacyId();
            int ingredientMeta = ((Integer) potionMix.get("reagentMeta"));
            legacyEntry = itemMapping.fromIdentifier(((String) potionMix.get("outputId")));
            if (legacyEntry == null) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown outputId: " + potionMix);
                }
                continue;
            }
            int toPotionId = legacyEntry.getLegacyId();
            int toPotionMeta = ((Integer) potionMix.get("outputMeta"));
            if (fromPotionId == 0 || ingredient == 0 || toPotionId == 0) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown potion mix: " + potionMix);
                }
                continue;
            }

            this.registerBrewingRecipe(new BrewingRecipe(Item.get(fromPotionId, fromPotionMeta), Item.get(ingredient, ingredientMeta), Item.get(toPotionId, toPotionMeta)));
        }

        for (Map containerMix : (List<Map>) root.get("containerMixes")) {
            RuntimeItemMapping.LegacyEntry legacyEntry;

            legacyEntry = itemMapping.fromIdentifier(((String) containerMix.get("inputId")));
            if (legacyEntry == null) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown inputId: " + containerMix);
                }
                continue;
            }
            int fromItemId = legacyEntry.getLegacyId();
            legacyEntry = itemMapping.fromIdentifier(((String) containerMix.get("reagentId")));
            if (legacyEntry == null) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown reagentId: " + containerMix);
                }
                continue;
            }
            int ingredient = legacyEntry.getLegacyId();
            legacyEntry = itemMapping.fromIdentifier(((String) containerMix.get("outputId")));
            if (legacyEntry == null) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown outputId: " + containerMix);
                }
                continue;
            }
            int toItemId = legacyEntry.getLegacyId();
            if (fromItemId == 0 || ingredient == 0 || toItemId == 0) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown container mix: " + containerMix);
                }
                continue;
            }

            this.registerContainerRecipe(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
        }

        ConfigSection smithing = new Config(Config.YAML).loadFromStream(Server.class.getClassLoader().getResourceAsStream("smithing.json")).getRootSection();

        top:
        for (Map<String, Object> recipe : (List<Map<String, Object>>) smithing.get((Object) "smithing")) {
            String recipeId = (String) recipe.get("id");
            Map<String, Object> first = ((List<Map>) recipe.get("output")).get(0);
            RuntimeItemMapping.LegacyEntry legacyEntry;
            legacyEntry = itemMapping.fromIdentifier((String) first.get("id"));
            if (legacyEntry == null) {
                if (Nukkit.DEBUG > 1) {
                    MainLogger.getLogger().debug("Unknown smithing output: " + recipe);
                }
                continue;
            }

            Item item = Item.get(legacyEntry.getLegacyId(), 0, 1);

            List<Item> ingredients = new ArrayList<>();
            for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                legacyEntry = itemMapping.fromIdentifier((String) ingredient.get("id"));
                if (legacyEntry == null) {
                    if (Nukkit.DEBUG > 1) {
                        MainLogger.getLogger().debug("Unknown smithing input: " + recipe);
                    }
                    continue top;
                }

                Item ing = Item.get(legacyEntry.getLegacyId(), 0, 1);
                ingredients.add(ing);
            }

            this.registerRecipe(new SmithingRecipe(recipeId, 0, ingredients, item));
        }
    }

    private static boolean needExpandLegacy(int id) {
        return id == BlockID.WOOL || id == BlockID.STONE || id == BlockID.PLANKS || id == BlockID.WOODEN_SLAB || id == ItemID.COAL;
    }

    @SuppressWarnings("unchecked")
    private void buildShapelessRecipeItemTagOverrides(RuntimeItemMapping itemMapping, List<Map> input, Item outputItem, String toReplaceTag, String replaceOtherTagKey, String replaceOtherTagValue) {
        Set<String> tags = MaterialTags.getVanillaDefinitions(toReplaceTag);
        if (tags == null) {
            if (Nukkit.DEBUG > 1) {
                MainLogger.getLogger().debug("Unknown item tag: " + toReplaceTag);
            }
            return;
        }

        top:
        for (String material : tags) {
            List<Item> sorted = new ArrayList<>();
            int expandLegacy = 0;

            for (Map<String, Object> ingredient : input) {
                Item inputItem;
                String type = (String) ingredient.get("type");

                if (!"default".equals(type)) {
                    if ("item_tag".equals(type)) {
                        String itemTag = (String) ingredient.get("itemTag");
                        if (!itemTag.equals(toReplaceTag)) {
                            if (itemTag.equals(replaceOtherTagKey)) {
                                RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromIdentifier(replaceOtherTagValue);
                                if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                                    if (Nukkit.DEBUG > 1) {
                                        MainLogger.getLogger().debug("Unknown multi item tag input: " + replaceOtherTagValue);
                                    }
                                    continue top;
                                }
                                inputItem = Item.get(legacyEntry.getLegacyId(), legacyEntry.getDamage(), (Integer) ingredient.getOrDefault("count", 1));
                                if (needExpandLegacy(legacyEntry.getLegacyId())) {
                                    expandLegacy = legacyEntry.getLegacyId();
                                }
                            } else {
                                buildShapelessRecipeItemTagOverrides(itemMapping, input, outputItem, itemTag, toReplaceTag, material);
                                continue top;
                            }
                        } else {
                            RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromIdentifier(material);
                            if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                                if (Nukkit.DEBUG > 1) {
                                    MainLogger.getLogger().debug("Unknown item tag input: " + material);
                                }
                                continue top;
                            }
                            inputItem = Item.get(legacyEntry.getLegacyId(), legacyEntry.getDamage(), (Integer) ingredient.getOrDefault("count", 1));
                            if (needExpandLegacy(legacyEntry.getLegacyId())) {
                                expandLegacy = legacyEntry.getLegacyId();
                            }
                        }
                    } else {
                        throw new RuntimeException("Unsupported type: " + type);
                    }
                } else {
                    RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromRuntime((int) ingredient.get("itemId"));
                    if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                        if (Nukkit.DEBUG > 1) {
                            MainLogger.getLogger().debug("Unknown shapeless input: " + input);
                        }
                        continue top;
                    }
                    int aux = (int) ingredient.getOrDefault("auxValue", 0);
                    if (aux == 32767) {
                        aux = legacyEntry.isHasDamage() ? legacyEntry.getDamage() : -1;
                    } else if (aux == 0) {
                        aux = legacyEntry.getDamage();
                    }
                    inputItem = Item.get(legacyEntry.getLegacyId(), aux, (Integer) ingredient.getOrDefault("count", 1));
                }

                sorted.add(inputItem);
            }

            sorted.sort(recipeComparator);

            if (expandLegacy != 0) {
                int lastMeta = expandLegacy == ItemID.COAL ? 1 : expandLegacy == BlockID.PLANKS || expandLegacy == BlockID.WOODEN_SLAB ? 5 : expandLegacy == BlockID.STONE ? 6 : 15;

                for (int meta = 0; meta <= lastMeta; meta++) {
                    for (Item item : sorted) {
                        if (item.getId() == expandLegacy) {
                            item.setDamage(meta);
                        }
                    }

                    this.registerRecipe(new ShapelessRecipe(null, 0, outputItem, sorted));
                }
            } else {
                this.registerRecipe(new ShapelessRecipe(null, 0, outputItem, sorted));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void buildShapedRecipeItemTagOverrides(RuntimeItemMapping itemMapping, Map<String, Map<String, Object>> input, String[] shape, Item outputItem, String toReplaceTag, String replaceOtherTagKey, String replaceOtherTagValue) {
        Set<String> tags = MaterialTags.getVanillaDefinitions(toReplaceTag);
        if (tags == null) {
            if (Nukkit.DEBUG > 1) {
                MainLogger.getLogger().debug("Unknown item tag: " + toReplaceTag);
            }
            return;
        }

        top:
        for (String material : tags) {
            Map<Character, Item> ingredients = new CharObjectHashMap<>();
            int expandLegacy = 0;

            for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
                Item inputItem;
                String type = (String) ingredientEntry.getValue().get("type");

                if (!"default".equals(type)) {
                    if ("item_tag".equals(type)) {
                        String itemTag = (String) ingredientEntry.getValue().get("itemTag");
                        if (!itemTag.equals(toReplaceTag)) {
                            if (itemTag.equals(replaceOtherTagKey)) {
                                RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromIdentifier(replaceOtherTagValue);
                                if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                                    if (Nukkit.DEBUG > 1) {
                                        MainLogger.getLogger().debug("Unknown multi item tag input: " + replaceOtherTagValue);
                                    }
                                    continue top;
                                }
                                inputItem = Item.get(legacyEntry.getLegacyId(), legacyEntry.getDamage(), (Integer) ingredientEntry.getValue().getOrDefault("count", 1));
                                if (needExpandLegacy(legacyEntry.getLegacyId())) {
                                    expandLegacy = legacyEntry.getLegacyId();
                                }
                            } else {
                                buildShapedRecipeItemTagOverrides(itemMapping, input, shape, outputItem, itemTag, toReplaceTag, material);
                                continue top;
                            }
                        } else {
                            RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromIdentifier(material);
                            if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                                if (Nukkit.DEBUG > 1) {
                                    MainLogger.getLogger().debug("Unknown item tag input: " + material);
                                }
                                continue top;
                            }
                            inputItem = Item.get(legacyEntry.getLegacyId(), legacyEntry.getDamage(), (Integer) ingredientEntry.getValue().getOrDefault("count", 1));
                            if (needExpandLegacy(legacyEntry.getLegacyId())) {
                                expandLegacy = legacyEntry.getLegacyId();
                            }
                        }
                    } else {
                        throw new RuntimeException("Unsupported type: " + type);
                    }
                } else {
                    RuntimeItemMapping.LegacyEntry legacyEntry = itemMapping.fromRuntime((int) ingredientEntry.getValue().get("itemId"));
                    if (legacyEntry == null || legacyEntry.getLegacyId() == 0) {
                        if (Nukkit.DEBUG > 1) {
                            MainLogger.getLogger().debug("Unknown shaped input: " + input);
                        }
                        continue top;
                    }
                    int aux = (int) ingredientEntry.getValue().getOrDefault("auxValue", 0);
                    if (aux == 32767) {
                        aux = legacyEntry.isHasDamage() ? legacyEntry.getDamage() : -1;
                    } else if (aux == 0) {
                        aux = legacyEntry.getDamage();
                    }
                    inputItem = Item.get(legacyEntry.getLegacyId(), aux, (Integer) ingredientEntry.getValue().getOrDefault("count", 1));
                }

                ingredients.put(ingredientEntry.getKey().charAt(0), inputItem);
            }

            if (expandLegacy != 0) {
                int lastMeta = expandLegacy == ItemID.COAL ? 1 : expandLegacy == BlockID.PLANKS || expandLegacy == BlockID.WOODEN_SLAB ? 5 : expandLegacy == BlockID.STONE ? 6 : 15;

                for (int meta = 0; meta <= lastMeta; meta++) {
                    for (Item item : ingredients.values()) {
                        if (item.getId() == expandLegacy) {
                            item.setDamage(meta);
                        }
                    }

                    this.registerRecipe(new ShapedRecipe(null, 0, outputItem, shape, ingredients, Collections.EMPTY_LIST));
                }
            } else {
                this.registerRecipe(new ShapedRecipe(null, 0, outputItem, shape, ingredients, Collections.EMPTY_LIST));
            }
        }
    }

    /**
     * Rebuild cached CraftingDataPacket for all protocols after the recipe list has been changed
     */
    public void rebuildPacket() {
        CraftingDataPacket pk = new CraftingDataPacket();
        for (Recipe recipe : this.recipes) {
            if (recipe instanceof ShapedRecipe) {
                pk.addShapedRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                pk.addShapelessRecipe((ShapelessRecipe) recipe);
            }
        }
        for (FurnaceRecipe recipe : this.furnaceRecipes.values()) {
            pk.addFurnaceRecipe(recipe);
        }
        for (BrewingRecipe recipe : this.brewingRecipes.values()) {
            pk.addBrewingRecipe(recipe);
        }
        for (ContainerRecipe recipe : this.containerRecipes.values()) {
            pk.addContainerRecipe(recipe);
        }
        // Note: Currently not implemented
        for (MultiRecipe recipe : this.multiRecipes.values()) {
            pk.addMultiRecipe(recipe);
        }
        pk.tryEncode();
        CraftingManager.packet = pk.compress(Deflater.BEST_COMPRESSION);
    }

    public BatchPacket getCachedPacket() {
        return packet;
    }

    /* Register recipes start */

    public void registerRecipe(Recipe recipe) {
        if (recipe instanceof CraftingRecipe) {
            UUID id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));
            ((CraftingRecipe) recipe).setId(id);
            this.recipes.add(recipe);
        }
        recipe.registerToCraftingManager(this);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapedRecipe> map;
        map = this.shapedRecipes.computeIfAbsent(resultHash, k -> new HashMap<>());
        map.put(getMultiItemHash(new LinkedList<>(recipe.getIngredientsAggregate())), recipe);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map;
        map = this.shapelessRecipes.computeIfAbsent(resultHash, k -> new HashMap<>());
        map.put(getMultiItemHash(recipe.getIngredientsAggregate()), recipe);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        this.furnaceRecipes.put(getItemHash(recipe.getInput()), recipe);
    }

    public void registerContainerRecipe(ContainerRecipe recipe) {
        this.containerRecipes.put(getContainerHash(recipe.getIngredient().getId(), recipe.getInput().getId()), recipe);
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        this.brewingRecipes.put(getPotionHash(recipe.getIngredient(), recipe.getInput()), recipe);
    }

    public void registerMultiRecipe(MultiRecipe recipe) {
        this.multiRecipes.put(recipe.getId(), recipe);
    }

    public void registerCampfireRecipe(CampfireRecipe recipe) {
        this.campfireRecipes.put(getItemHash(recipe.getInput()), recipe);
    }

    public void registerSmithingRecipe(SmithingRecipe recipe) {
        this.smithingRecipes.put(getMultiItemHash(recipe.getIngredientsAggregate()), recipe);
    }

    /* Match recipes start */

    public CraftingRecipe matchRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        int outputHash = getItemHash(primaryOutput);


        Map<UUID, ShapedRecipe> shapedRecipeMap = this.shapedRecipes.get(outputHash);

        if (shapedRecipeMap != null) {
            inputList.sort(recipeComparator);
            UUID inputHash = getMultiItemHash(inputList);
            ShapedRecipe recipe = shapedRecipeMap.get(inputHash);
            if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                return recipe;
            }
            for (ShapedRecipe shapedRecipe : shapedRecipeMap.values()) {
                if (shapedRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapedRecipe, inputList, primaryOutput, extraOutputList)) {
                    return shapedRecipe;
                }
            }
        }

        Map<UUID, ShapelessRecipe> shapelessRecipeMap = this.shapelessRecipes.get(outputHash);

        if (shapelessRecipeMap != null) {
            inputList.sort(recipeComparator);
            UUID inputHash = getMultiItemHash(inputList);
            ShapelessRecipe recipe = shapelessRecipeMap.get(inputHash);
            if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                return recipe;
            }
            for (ShapelessRecipe shapelessRecipe : shapelessRecipeMap.values()) {
                if (shapelessRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapelessRecipe, inputList, primaryOutput, extraOutputList)) {
                    return shapelessRecipe;
                }
            }
        }

        return null;
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        FurnaceRecipe recipe = this.furnaceRecipes.get(getItemHash(input));
        if (recipe == null) {
            recipe = this.furnaceRecipes.get(getItemHash(input.getId(), 0));
        }
        return recipe;
    }

    public ContainerRecipe matchContainerRecipe(Item input, Item potion) {
        return this.containerRecipes.get(getContainerHash(input.getId(), potion.getId()));
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        return this.brewingRecipes.get(getPotionHash(input, potion));
    }

    public CampfireRecipe matchCampfireRecipe(Item input) {
        CampfireRecipe recipe = this.campfireRecipes.get(getItemHash(input));
        if (recipe == null) {
            recipe = this.campfireRecipes.get(getItemHash(input.getId(), 0));
        }
        return recipe;
    }

    private static boolean matchItemsAccumulation(CraftingRecipe recipe, List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        Item recipeResult = recipe.getResult();
        if (primaryOutput.equals(recipeResult, recipeResult.hasMeta(), recipeResult.hasCompoundTag()) && primaryOutput.getCount() % recipeResult.getCount() == 0) {
            int multiplier = primaryOutput.getCount() / recipeResult.getCount();
            return recipe.matchItems(inputList, extraOutputList, multiplier);
        }
        return false;
    }

    public SmithingRecipe matchSmithingRecipe(List<Item> inputList) {
        inputList.sort(recipeComparator);

        SmithingRecipe recipe = this.smithingRecipes.get(getMultiItemHash(inputList));
        if (recipe != null && recipe.matchItems(inputList)) {
            return recipe;
        }

        ArrayList<Item> list = new ArrayList<>(inputList.size());
        for (Item item : inputList) {
            Item clone = item.clone();
            clone.setCount(1);
            if (item instanceof ItemDurable && item.getDamage() > 0) {
                clone.setDamage(0);
            }
            list.add(clone);
        }

        for (SmithingRecipe smithingRecipe : this.smithingRecipes.values()) {
            if (smithingRecipe.matchItems(list)) {
                return smithingRecipe;
            }
        }

        return null;
    }

    /* Hash items start */

    private static int getItemHash(Item item) {
        return getItemHash(item.getId(), item.getDamage());
    }

    private static int getItemHash(int id, int meta) {
        return (id << 12) | (meta & 0xfff);
    }

    private static int getFullItemHash(Item item) {
        return (getItemHash(item) << 6) | (item.getCount() & 0x3f);
    }

    private static UUID getMultiItemHash(Collection<Item> items) {
        BinaryStream stream = new BinaryStream(new byte[5 * items.size()]).reset();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    private static int getContainerHash(int ingredientId, int containerId) {
        return (ingredientId << 15) | containerId;
    }

    private static int getPotionHash(Item ingredient, Item potion) {
        int ingredientHash = ((ingredient.getId() & 0x3FF) << 6) | (ingredient.getDamage() & 0x3F);
        int potionHash = ((potion.getId() & 0x3FF) << 6) | (potion.getDamage() & 0x3F);
        return ingredientHash << 16 | potionHash;
    }
}
