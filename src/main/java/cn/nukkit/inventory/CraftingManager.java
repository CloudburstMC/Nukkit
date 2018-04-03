package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.Deflater;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingManager {

    public final Collection<Recipe> recipes = new ArrayDeque<>();

    public static BatchPacket packet = null;
    protected final Map<Integer, Map<UUID, ShapedRecipe>> shapedRecipes = new Int2ObjectOpenHashMap<>();

    public final Map<Integer, FurnaceRecipe> furnaceRecipes = new Int2ObjectOpenHashMap<>();

    public final Map<Integer, BrewingRecipe> brewingRecipes = new Int2ObjectOpenHashMap<>();

    private static int RECIPE_COUNT = 0;
    protected final Map<Integer, Map<UUID, ShapelessRecipe>> shapelessRecipes = new Int2ObjectOpenHashMap<>();

    public static final Comparator<Item> recipeComparator = (i1, i2) -> {
        if (i1.getId() > i2.getId()) {
            return 1;
        } else if (i1.getId() < i2.getId()) {
            return -1;
        } else if (i1.getDamage() > i2.getDamage()) {
            return 1;
        } else if (i1.getDamage() < i2.getDamage()) {
            return -1;
        } else return Integer.compare(i1.getCount(), i2.getCount());
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
                        List<Item> sorted = new ArrayList<>();
                        for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                            sorted.add(Item.fromJson(ingredient));
                        }
                        // Bake sorted list
                        sorted.sort(recipeComparator);

                        ShapelessRecipe result = new ShapelessRecipe(Item.fromJson(first), sorted);

                        this.registerRecipe(result);
                        break;
                    case 1:
                        List<Map> output = (List<Map>) recipe.get("output");

                        first = output.remove(0);
                        String[] shape = ((List<String>) recipe.get("shape")).stream().toArray(String[]::new);
                        Map<Character, Item> ingredients = new CharObjectHashMap<>();
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
        pk.cleanRecipes = true;

        for (Recipe recipe : this.getRecipes()) {
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

        packet = pk.compress(Deflater.BEST_COMPRESSION);
    }

    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    public Map<Integer, FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        FurnaceRecipe recipe = this.furnaceRecipes.get(getItemHash(input));
        if (recipe == null) this.furnaceRecipes.get(getItemHash(input.getId(), 0));
        return recipe;
    }

    private static UUID getMultiItemHash(Collection<Item> items) {
        BinaryStream stream = new BinaryStream();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    private static int getFullItemHash(Item item) {
        return getItemHash(item) + item.getCount() << 10;
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        this.furnaceRecipes.put(getItemHash(input), recipe);
    }

    private static int getItemHash(Item item) {
        return getItemHash(item.getId(), item.getDamage());
    }

    private static int getItemHash(int id, int meta) {
        return id + (meta << 8);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapedRecipe> map = shapedRecipes.get(resultHash);
        if (map == null) {
            map = new HashMap<>();
            shapedRecipes.put(resultHash, map);
        }
        map.put(getMultiItemHash(recipe.getIngredientList()), recipe);
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
            this.recipes.add(recipe);
        }

        recipe.registerToCraftingManager(this);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        List<Item> list = recipe.getIngredientList();
        list.sort(recipeComparator);

        UUID hash = getMultiItemHash(list);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map = shapelessRecipes.computeIfAbsent(resultHash, k -> new HashMap<>());

        map.put(hash, recipe);
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        Item input = recipe.getInput();
        Item potion = recipe.getPotion();

        this.brewingRecipes.put(getItemHash(input.getId(), potion.getDamage()), recipe);
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        return brewingRecipes.get(getItemHash(input.getId(), potion.getDamage()));
    }

    public CraftingRecipe matchRecipe(Item[][] inputMap, Item primaryOutput, Item[][] extraOutputMap) {
        //TODO: try to match special recipes before anything else (first they need to be implemented!)

        int outputHash = getItemHash(primaryOutput);
        if (this.shapedRecipes.containsKey(outputHash)) {
            List<Item> itemCol = new ArrayList<>();
            for (Item[] items : inputMap) itemCol.addAll(Arrays.asList(items));
            UUID inputHash = getMultiItemHash(itemCol);

            Map<UUID, ShapedRecipe> recipeMap = shapedRecipes.get(outputHash);

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
            list.sort(recipeComparator);

            UUID inputHash = getMultiItemHash(list);

            Map<UUID, ShapelessRecipe> recipes = shapelessRecipes.get(outputHash);

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
