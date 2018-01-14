package cn.nukkit.server.inventory;

import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.block.BlockAir;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemPotion;
import cn.nukkit.server.network.protocol.CraftingDataPacket;
import cn.nukkit.server.util.Config;
import cn.nukkit.server.util.Utils;

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

    public final Map<String, FurnaceRecipe> furnaceRecipes = new HashMap<>();

    public final Map<String, BrewingRecipe> brewingRecipes = new HashMap<>();

    private static int RECIPE_COUNT = 0;

    public static CraftingDataPacket packet = null;

    public CraftingManager() {
        String path = NukkitServer.getInstance().getDataPath() + "recipes.json";

        if (!new File(path).exists()) {
            try {
                Utils.writeFile(path, NukkitServer.class.getClassLoader().getResourceAsStream("recipes.json"));
            } catch (IOException e) {
                MainLogger.getLogger().logException(e);
            }
        }

        List<Map> recipes = new Config(path, Config.JSON).getMapList("recipes");
        MainLogger.getLogger().info("Loading recipes...");
        for (Map<String, Object> recipe : recipes) { //TODO: implement this better
            switch (Utils.toInt(recipe.get("type"))) {
                case 0:
                    // TODO: handle multiple result items
                    Map<String, Object> first = ((List<Map>) recipe.get("output")).get(0);
                    ShapelessRecipe result = new ShapelessRecipe(Item.get(Utils.toInt(first.get("id")), Utils.toInt(first.get("damage")), Utils.toInt(first.get("count")), first.get("nbt").toString().getBytes()));
                    for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                        result.addIngredient(Item.get(Utils.toInt(ingredient.get("id")), Utils.toInt(ingredient.get("damage")), Utils.toInt(ingredient.get("count")), ingredient.get("nbt").toString().getBytes()));
                    }

                    String id = (String) recipe.get("uuid");
                    if (id != null && !id.isEmpty()) {
                        UUID uuid = UUID.fromString(id);
                        result.setId(uuid);
                        this.registerShapelessRecipe(result);
                    }
                    break;
                case 1:
                    // TODO: handle multiple result items
                    first = ((List<Map>) recipe.get("output")).get(0);
                    ShapedRecipe shapedRecipe = new ShapedRecipe(Item.get(Utils.toInt(first.get("id")), Utils.toInt(first.get("damage")), Utils.toInt(first.get("count")), first.get("nbt").toString().getBytes()), Utils.toInt(recipe.get("height")), Utils.toInt(recipe.get("width")));
                    Object[][] shape = Utils.splitArray(((List) recipe.get("input")).stream().toArray(), Utils.toInt(recipe.get("width")));
                    for (int y = 0; y < shape.length; y++) {
                        Object[] row = shape[y];
                        for (int x = 0; x < row.length; x++) {
                            Object data = row[x];

                            if (data instanceof Map) {
                                Map<String, Object> ingredient = (Map) data;
                                shapedRecipe.addIngredient(x, y, Item.get(Utils.toInt(ingredient.get("id")), Utils.toInt(ingredient.get("damage")), Utils.toInt(ingredient.get("count")), ingredient.get("nbt").toString().getBytes()));
                            } else {
                                shapedRecipe.addIngredient(x, y, new ItemBlock(new BlockAir()));
                            }
                        }
                    }

                    id = (String) recipe.get("uuid");

                    if (id != null && !id.isEmpty()) {
                        UUID uuid = UUID.fromString(id);
                        shapedRecipe.setId(uuid);
                        this.registerShapedRecipe(shapedRecipe);
                    }
                    break;
                case 2:
                case 3:
                    Map<String, Object> resultMap = (Map) recipe.get("output");
                    Item resultItem = Item.get(Utils.toInt(resultMap.get("id")), Utils.toInt(resultMap.get("damage")), Utils.toInt(resultMap.get("count")), ((String) resultMap.get("nbt")).getBytes());
                    this.registerRecipe(new FurnaceRecipe(resultItem, Item.get(Utils.toInt(recipe.get("inputId")), recipe.containsKey("inputDamage") ? Utils.toInt(recipe.get("inputDamage")) : 0, 1)));
                    break;
                default:
                    break;
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
        pk.cleanRecipes = true;

        for (Recipe recipe : this.getRecipes().values()) {
            if (recipe instanceof ShapedRecipe) {
                pk.addShapedRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                pk.addShapelessRecipe((ShapelessRecipe) recipe);
            }
        }

        for (FurnaceRecipe recipe : this.getFurnaceRecipes().values()) {
            pk.addFurnaceRecipe(recipe);
        }

        pk.encode();
        pk.isEncoded = true;

        packet = pk;
    }

    public final Comparator<Item> comparator = (i1, i2) -> {
        if (i1.getId() > i2.getId()) {
            return 1;
        } else if (i1.getId() < i2.getId()) {
            return -1;
        } else if (i1.getDamage() > i2.getDamage()) {
            return 1;
        } else if (i1.getDamage() < i2.getDamage()) {
            return -1;
        }
        return Integer.compare(i1.getCount(), i2.getCount());
    };

    public Recipe getRecipe(UUID id) {
        return this.recipes.getOrDefault(id, null);
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
        Item result = recipe.getResult();
        this.recipes.put(recipe.getId(), recipe);
        Map<Integer, Map<Integer, Item>> ingredients = recipe.getIngredientMap();
        String hash = "";
        for (Map<Integer, Item> v : ingredients.values()) {
            for (Item item : v.values()) {
                if (item != null && item.getId() != Item.AIR) {
                    hash += item.getId() + ":" + (!item.hasMeta() ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
                }
            }

            hash += ";";
        }

        String index = result.getId() + ":" + (result.hasMeta() ? result.getDamage() : "");
        if (!this.recipeLookup.containsKey(index)) {
            this.recipeLookup.put(index, new HashMap<>());
        }

        this.recipeLookup.get(index).put(hash, recipe);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        Item result = recipe.getResult();
        this.recipes.put(recipe.getId(), recipe);
        String hash = "";
        List<Item> ingredients = recipe.getIngredientList();
        ingredients.sort(this.comparator);
        for (Item item : ingredients) {
            hash += item.getId() + ":" + (!item.hasMeta() ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
        }

        if (!this.recipeLookup.containsKey(result.getId() + ":" + result.getDamage())) {
            this.recipeLookup.put(result.getId() + ":" + result.getDamage(), new HashMap<>());
        }
        this.recipeLookup.get(result.getId() + ":" + result.getDamage()).put(hash, recipe);
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

    public boolean matchRecipe(ShapelessRecipe recipe) {
        String idx = recipe.getResult().getId() + ":" + recipe.getResult().getDamage();
        if (!this.recipeLookup.containsKey(idx)) {
            return false;
        }

        String hash = "";
        List<Item> ingredients = recipe.getIngredientList();
        ingredients.sort(this.comparator);
        for (Item item : ingredients) {
            hash += item.getId() + ":" + (!item.hasMeta() ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
        }

        if (this.recipeLookup.get(idx).containsKey(hash)) {
            return true;
        }

        Recipe hasRecipe = null;

        for (Recipe r : this.recipeLookup.get(idx).values()) {
            if (r instanceof ShapelessRecipe) {
                if (((ShapelessRecipe) r).getIngredientCount() != ingredients.size()) {
                    continue;
                }
                List<Item> checkInput = ((ShapelessRecipe) r).getIngredientList();
                for (Item item : ingredients) {
                    int amount = item.getCount();
                    for (Item checkItem : checkInput) {
                        if (checkItem.equals(item, checkItem.hasMeta())) {
                            int remove = Math.min(checkItem.getCount(), amount);
                            checkItem.setCount(checkItem.getCount() - amount);
                            if (checkItem.getCount() == 0) {
                                checkInput.remove(checkItem);
                            }
                            amount -= remove;
                            if (amount == 0) {
                                break;
                            }
                        }
                    }
                }

                if (checkInput.isEmpty()) {
                    hasRecipe = r;
                    break;
                }
            }
        }
        return hasRecipe != null;
    }

    public void registerRecipe(Recipe recipe) {
        //recipe.setId(Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag())));

        if (recipe instanceof ShapedRecipe) {
            this.registerShapedRecipe((ShapedRecipe) recipe);
        } else if (recipe instanceof ShapelessRecipe) {
            this.registerShapelessRecipe((ShapelessRecipe) recipe);
        } else if (recipe instanceof FurnaceRecipe) {
            this.registerFurnaceRecipe((FurnaceRecipe) recipe);
        }
    }

    public Recipe[] getRecipesByResult(Item result) {
        Map<String, Recipe> lookup = recipeLookup.get(result.getId() + ":" + result.getDamage());
        if (lookup == null) return new Recipe[0];
        return recipeLookup.get(result.getId() + ":" + result.getDamage()).values().stream().toArray(Recipe[]::new);
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
