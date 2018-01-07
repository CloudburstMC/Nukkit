package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.PlayerProtocol;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingManager {

    public final Map<UUID, Recipe> recipes = new HashMap<>();

    protected final Map<String, Map<String, Recipe>> recipeLookup = new HashMap<>();
    protected final Map<String, Map<String, ShapedRecipe>> shapedRecipes = new HashMap<>();
    protected final Map<String, Map<String, ShapelessRecipe>> shapelessRecipes = new HashMap<>();

    public final Map<String, FurnaceRecipe> furnaceRecipes = new HashMap<>();

    public final Map<String, BrewingRecipe> brewingRecipes = new HashMap<>();

    private static int RECIPE_COUNT = 0;

    public static CraftingDataPacket packet113 = null;
    public static CraftingDataPacket packet = null;

    private static final Comparator<Item> recipeComparator = (i1, i2) -> {
        if (i1.getId() > i2.getId()) {
            return 1;
        } else if (i1.getId() < i2.getId()) {
            return -1;
        } else if (i1.getDamage() > i2.getDamage()) {
            return 1;
        } else if (i1.getDamage() < i2.getDamage()) {
            return -1;
        } else if (i1.getCount() > i2.getCount()) {
            return 1;
        } else if (i1.getCount() < i2.getCount()) {
            return -1;
        } else {
            return 0;
        }
    };

    @SuppressWarnings("unchecked")
    public CraftingManager() {
        String path = Server.getInstance().getDataPath() + "recipes.json";

        if (!new File(path).exists()) {
            try {
                Utils.writeFile(path, Server.class.getClassLoader().getResourceAsStream("recipes.json"));
            } catch (IOException e) {
                MainLogger.getLogger().logException(e);
            }
        }

        List<Map> recipes = new Config(path, Config.JSON).getMapList("recipes");
        MainLogger.getLogger().info("Loading recipes...");
        for (Map<String, Object> recipe : recipes) {
            try {
                switch (Utils.toInt(recipe.get("type"))) {
                    case 0:
                        // TODO: handle multiple result items
                        Map<String, Object> first = ((List<Map>) recipe.get("output")).get(0);
                        ShapelessRecipe result = new ShapelessRecipe(Item.fromJson(first));

                        for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                            result.addIngredient(Item.fromJson(ingredient));
                        }

                        this.registerRecipe(result);
                        break;
                    case 1:
                        List<Map> output = (List<Map>) recipe.get("output");

                        first = output.remove(0);
                        String[] shape = ((List<String>) recipe.get("shape")).stream().toArray(String[]::new);
                        Map<Character, Item> ingredients = new HashMap<>();
                        List<Item> extraResults = new ArrayList<>();

                        Map<String, Map<String, Object>> input = (Map) recipe.get("input");
                        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
                            char ingredientChar = ingredientEntry.getKey().charAt(0);
                            Item ingredient = Item.fromJson(ingredientEntry.getValue());

                            ingredients.put(ingredientChar, ingredient);
                        }

                        for (Map<String, Object> data : output) {
                            extraResults.add(Item.fromJson(data));
                        }

                        this.registerRecipe(new ShapedRecipe(Item.fromJson(first), shape, ingredients, extraResults));
                        break;
                    case 2:
                    case 3:
                        Map<String, Object> resultMap = (Map) recipe.get("output");
                        Item resultItem = Item.fromJson(resultMap);
                        this.registerRecipe(new FurnaceRecipe(resultItem, Item.get(Utils.toInt(recipe.get("inputId")), recipe.containsKey("inputDamage") ? Utils.toInt(recipe.get("inputDamage")) : -1, 1)));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                MainLogger.getLogger().error("Exception during registering recipe", e);
            }
        }

        this.registerBrewing();
        this.rebuildPacket();

        MainLogger.getLogger().info("Loaded " + this.recipes.size() + " recipes.");
    }

    protected void registerBrewing() {
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.AWKWARD, 1), Item.get(Item.NETHER_WART, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.THICK, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.MUNDANE_II, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH, 1), Item.get(Item.BLAZE_POWDER, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH_II, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.WEAKNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.WEAKNESS_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.WEAKNESS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.NIGHT_VISION, 1), Item.get(Item.GOLDEN_CARROT, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.NIGHT_VISION_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.NIGHT_VISION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INVISIBLE, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.NIGHT_VISION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INVISIBLE_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.INVISIBLE, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INVISIBLE_LONG, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.NIGHT_VISION_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE, 1), Item.get(Item.MAGMA_CREAM, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.LEAPING, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS_LONG, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS_LONG, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SPEED, 1), Item.get(Item.SUGAR, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SPEED_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SPEED_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH, 1), Item.get(Item.GLISTERING_MELON, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.POISON, 1), Item.get(Item.SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.POISON_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.POISON, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.POISON_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.POISON, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.REGENERATION, 1), Item.get(Item.GHAST_TEAR, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.REGENERATION_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.REGENERATION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.REGENERATION_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.REGENERATION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.WATER_BREATHING, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.POISON, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.HARMING, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING_II, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH_II, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING_II, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.POISON_LONG, 1)));
    }

    public void rebuildPacket() {
        CraftingDataPacket pk = new CraftingDataPacket();
        CraftingDataPacket pk113 = new CraftingDataPacket();
        pk.cleanRecipes = true;
        pk113.cleanRecipes = true;

        for (Recipe recipe : this.getRecipes().values()) {
            if (recipe instanceof ShapedRecipe) {
                pk.addShapedRecipe((ShapedRecipe) recipe);
                pk113.addShapedRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                pk.addShapelessRecipe((ShapelessRecipe) recipe);
                pk113.addShapelessRecipe((ShapelessRecipe) recipe);
            }
        }

        for (FurnaceRecipe recipe : this.getFurnaceRecipes().values()) {
            pk.addFurnaceRecipe(recipe);
            pk113.addFurnaceRecipe((FurnaceRecipe) recipe);
        }

        pk.encode(PlayerProtocol.PLAYER_PROTOCOL_130);
        pk.isEncoded = true;

        pk113.encode(PlayerProtocol.PLAYER_PROTOCOL_113);
        pk113.isEncoded = true;

        packet113 = pk113;
        packet = pk;
    }

    public Recipe getRecipe(UUID id) {
        return this.recipes.containsKey(id) ? this.recipes.get(id) : null;
    }
    public Recipe[] getRecipesByResult(Item... output){
        if (output.length == 0) return new Recipe[0];
        return this.recipes.values().stream().filter(recipe -> {
            if (recipe instanceof CraftingRecipe) {
                List<Item> neededItems = ((CraftingRecipe) recipe).getAllResults();
                int needMatches = output.length;
                for (Item outputItem : output) {
                    needMatches--;
                    if (neededItems.contains(outputItem)) neededItems.remove(outputItem);
                }
                return (needMatches == 0 && neededItems.size() == 0);
            }
            return recipe.getResult().equals(output[0]) && output.length == 1;
        }).toArray(Recipe[]::new);
    }

    public Map<UUID, Recipe> getRecipes() {
        return recipes;
    }

    public Map<String, FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        if (this.furnaceRecipes.containsKey(input.getId() + ":" + input.getDamage())) {
            return this.furnaceRecipes.get(input.getId() + ":" + input.getDamage());
        } else if (this.furnaceRecipes.containsKey(input.getId() + ":?")) {
            return this.furnaceRecipes.get(input.getId() + ":?");
        }

        return null;
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        String resultHash = getItemHash(recipe.getResult());
        Map<String, ShapedRecipe> map = shapedRecipes.get(resultHash);
        if (map == null) {
            map = new HashMap<>();
            shapedRecipes.put(resultHash, map);
        }

        String inputHash = "";
        for (Map<Integer, Item> itemMap : recipe.getIngredientMap().values()) {
            for (Item item : itemMap.values()) {
                inputHash += getItemHash(item) + ",";
            }
        }
        inputHash += ";";

        map.put(inputHash, recipe);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        List<Item> list = recipe.getIngredientList();
        Collections.sort(list, recipeComparator);

        String inputHash = "";
        for (Item item : list) {
            inputHash += getItemHash(item) + ",";
        }
        inputHash += ";";

        String resultHash = getItemHash(recipe.getResult());
        Map<String, ShapelessRecipe> map = shapelessRecipes.get(resultHash);
        if (map == null) {
            map = new HashMap<>();
            shapelessRecipes.put(resultHash, map);
        }

        map.put(inputHash, recipe);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        this.furnaceRecipes.put(input.getId() + ":" + (!input.hasMeta() ? "?" : input.getDamage()), recipe);
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        Item input = recipe.getInput();
        Item potion = recipe.getPotion();

        this.brewingRecipes.put(input.getId() + ":" + (!potion.hasMeta() ? 0 : potion.getDamage()), recipe);
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        if (brewingRecipes.containsKey(input.getId() + ":" + (!potion.hasMeta() ? 0 : potion.getDamage()))) {
            return brewingRecipes.get(input.getId() + ":" + (!potion.hasMeta() ? 0 : potion.getDamage()));
        }
        return null;
    }

    public CraftingRecipe matchRecipe(Item[][] inputMap, Item primaryOutput, Item[][] extraOutputMap) {
        //TODO: try to match special recipes before anything else (first they need to be implemented!)

        String outputHash = getItemHash(primaryOutput);
        if (this.shapedRecipes.containsKey(outputHash)) {
            String inputHash = "";
            for (Item[] items : inputMap) {
                for (Item item : items) {
                    inputHash += getItemHash(item) + ",";
                }
            }
            inputHash += ";";

            Map<String, ShapedRecipe> recipeMap = shapedRecipes.get(outputHash);

            if (recipeMap != null) {
                ShapedRecipe recipe = recipeMap.get(inputHash);

                if (recipe != null && recipe.matchItems(this.cloneItemMap(inputMap), this.cloneItemMap(extraOutputMap))) { //matched a recipe by hash
                    return recipe;
                }

                for (ShapedRecipe shapedRecipe : recipeMap.values()) {
                    if (shapedRecipe.matchItems(this.cloneItemMap(inputMap), this.cloneItemMap(extraOutputMap))) {
                        return shapedRecipe;
                    }
                }
            }
        }

        if (shapelessRecipes.containsKey(outputHash)) {
            List<Item> list = new ArrayList<>();
            for (Item[] a : inputMap) {
                list.addAll(Arrays.asList(a));
            }
            Collections.sort(list, recipeComparator);

            String inputHash = "";
            for (Item item : list) {
                inputHash += getItemHash(item) + ",";
            }
            inputHash += ";";

            Map<String, ShapelessRecipe> recipes = shapelessRecipes.get(outputHash);

            if (recipes == null) {
                return null;
            }

            ShapelessRecipe recipe = recipes.get(inputHash);

            if (recipe != null && recipe.matchItems(this.cloneItemMap(inputMap), this.cloneItemMap(extraOutputMap))) {
                return recipe;
            }

            for (ShapelessRecipe shapelessRecipe : recipes.values()) {
                if (shapelessRecipe.matchItems(this.cloneItemMap(inputMap), this.cloneItemMap(extraOutputMap))) {
                    return shapelessRecipe;
                }
            }
        }

        return null;
    }

    private Item[][] cloneItemMap(Item[][] map) {
        Item[][] newMap = new Item[map.length][];
        for (int i = 0; i < newMap.length; i++) {
            Item[] old = map[i];
            Item[] n = new Item[old.length];

            System.arraycopy(old, 0, n, 0, n.length);
            newMap[i] = n;
        }

        for (int y = 0; y < newMap.length; y++) {
            Item[] row = newMap[y];
            for (int x = 0; x < row.length; x++) {
                Item item = newMap[y][x];
                newMap[y][x] = item.clone();
            }
        }
        return newMap;
    }

    public void registerRecipe(Recipe recipe) {
        if (recipe instanceof CraftingRecipe) {
            UUID id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));

            ((CraftingRecipe) recipe).setId(id);
            this.recipes.put(id, recipe);
        }

        recipe.registerToCraftingManager(this);
    }

    public static String getItemHash(Item item) {
        return item.getId() + ":" + (item.hasMeta() ? item.getDamage() : "");
    }

    public static class Entry {
        final int resultItemId;
        final int resultMeta;
        final int ingredientItemId;
        final int ingredientMeta;
        final String recipeShape;
        final int resultAmount;

        public Entry(int resultItemId, int resultMeta, int ingredientItemId, int ingredientMeta, String recipeShape, int resultAmount) {
            this.resultItemId = resultItemId;
            this.resultMeta = resultMeta;
            this.ingredientItemId = ingredientItemId;
            this.ingredientMeta = ingredientMeta;
            this.recipeShape = recipeShape;
            this.resultAmount = resultAmount;
        }
    }
}
