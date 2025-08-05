package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.*;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.*;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.zip.Deflater;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class CraftingManager {

    public final Collection<Recipe> recipes = new ArrayDeque<>();

    public static DataPacket packet;
    protected final Map<Integer, Map<UUID, ShapedRecipe>> shapedRecipes = new Int2ObjectOpenHashMap<>();
    protected final Map<Integer, Map<UUID, ShapelessRecipe>> shapelessRecipes = new Int2ObjectOpenHashMap<>();

    public final Map<UUID, MultiRecipe> multiRecipes = new HashMap<>();
    public final Map<Integer, FurnaceRecipe> furnaceRecipes = new Int2ObjectOpenHashMap<>();
    public final Map<Integer, BrewingRecipe> brewingRecipes = new Int2ObjectOpenHashMap<>();
    public final Map<Integer, ContainerRecipe> containerRecipes = new Int2ObjectOpenHashMap<>();
    @Getter
    private final Map<Integer, CampfireRecipe> campfireRecipes = new Int2ObjectOpenHashMap<>(); // Server only
    @Getter
    private final Map<UUID, SmithingRecipe> smithingRecipes = new HashMap<>();

    private static int RECIPE_COUNT = 0;
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
        } else return Integer.compare(i1.getCount(), i2.getCount());
    };

    public CraftingManager() {
        InputStream recipesStream = Server.class.getClassLoader().getResourceAsStream("recipes.json");
        if (recipesStream == null) {
            throw new AssertionError("Unable to find recipes.json");
        }

        Config recipesConfig = new Config(Config.JSON);
        recipesConfig.load(recipesStream);
        this.loadRecipes(recipesConfig);

        String path = Server.getInstance().getDataPath() + "custom_recipes.json";
        File filePath = new File(path);

        if (filePath.exists()) {
            Config customRecipes = new Config(filePath, Config.JSON);
            this.loadRecipes(customRecipes);
        }

        this.registerSmithingRecipes();

        this.rebuildPacket();

        MainLogger.getLogger().info("Successfully loaded " + this.recipes.size() + " recipes");
    }

    private void registerSmithingRecipes() {
        ConfigSection smithing = new Config(Config.YAML).loadFromStream(Server.class.getClassLoader().getResourceAsStream("smithing.json")).getRootSection();
        for (Map<String, Object> recipe : (List<Map<String, Object>>) smithing.get((Object) "smithing")) {
            List<Map> outputs = ((List<Map>) recipe.get("output"));
            if (outputs.size() > 1) {
                continue;
            }

            String recipeId = (String) recipe.get("id");

            Map<String, Object> first = outputs.get(0);
            Item item = Item.get(RuntimeItems.getMapping().fromIdentifier((String) first.get("id")).getLegacyId(), 0, 1);

            List<Item> ingredients = new ArrayList<>();
            for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                Item ing = Item.get(RuntimeItems.getMapping().fromIdentifier((String) ingredient.get("id")).getLegacyId(), 0, 1);
                ingredients.add(ing);
            }

            this.registerRecipe(new SmithingRecipe(recipeId, 0, ingredients, item));
            this.registerRecipe(new SmithingRecipe(recipeId, 0, ingredients, item));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRecipes(Config config) {
        List<Map> recipes = config.getMapList("recipes");
        MainLogger.getLogger().info("Loading recipes...");
        for (Map<String, Object> recipe : recipes) {
            try {
                switch (Utils.toInt(recipe.get("type"))) {
                    case 0:
                        String craftingBlock = (String) recipe.get("block");
                        if (!"crafting_table".equals(craftingBlock)) {
                            // Ignore other recipes than crafting table ones
                            continue;
                        }
                        // TODO: handle multiple result items
                        List<Map> outputs = ((List<Map>) recipe.get("output"));
                        if (outputs.size() > 1) {
                            continue;
                        }
                        Map<String, Object> first = outputs.get(0);
                        List<Item> sorted = new ArrayList<>();
                        for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                            sorted.add(Item.fromJson(ingredient));
                        }
                        // Bake sorted list
                        sorted.sort(recipeComparator);

                        Item resultItem = Item.fromJson(first);
                        this.registerRecipe(new ShapelessRecipe(null, Utils.toInt(recipe.get("priority")), resultItem, sorted)); // null recipeId will be replaced with recipe uuid
                        break;
                    case 1:
                        craftingBlock = (String) recipe.get("block");
                        if (!"crafting_table".equals(craftingBlock)) {
                            // Ignore other recipes than crafting table ones
                            continue;
                        }
                        outputs = (List<Map>) recipe.get("output");

                        first = outputs.remove(0);
                        String[] shape = ((List<String>) recipe.get("shape")).toArray(new String[0]);
                        Map<Character, Item> ingredients = new CharObjectHashMap<>();
                        List<Item> extraResults = new ArrayList<>();

                        Map<String, Map<String, Object>> input = (Map) recipe.get("input");
                        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
                            char ingredientChar = ingredientEntry.getKey().charAt(0);
                            Item ingredient = Item.fromJson(ingredientEntry.getValue());
                            ingredients.put(ingredientChar, ingredient);
                        }

                        for (Map<String, Object> data : outputs) {
                            extraResults.add(Item.fromJson(data));
                        }

                        resultItem = Item.fromJson(first);
                        this.registerRecipe(new ShapedRecipe(null, Utils.toInt(recipe.get("priority")), resultItem, shape, ingredients, extraResults));
                        break;
                    case 3:
                        craftingBlock = (String) recipe.get("block");
                        if (!"furnace".equals(craftingBlock) && !"campfire".equals(craftingBlock)) {
                            continue;
                        }
                        Map<String, Object> resultMap = (Map) recipe.get("output");
                        resultItem = Item.fromJson(resultMap);
                        Map<String, Object> inputMap = (Map) recipe.get("input");
                        Item inputItem = Item.fromJson(inputMap);
                        switch (craftingBlock) {
                            case "furnace":
                                this.registerRecipe(new FurnaceRecipe(resultItem, inputItem));
                                break;
                            case "campfire":
                                this.registerRecipe(new CampfireRecipe(resultItem, inputItem));
                                break;
                        }
                        break;
                    case 4:
                        this.registerRecipe(new MultiRecipe(UUID.fromString((String) recipe.get("uuid"))));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                MainLogger.getLogger().error("Exception during registering recipe", e);
            }
        }

        // Load brewing recipes
        List<Map> potionMixes = config.getMapList("potionMixes");

        for (Map potionMix : potionMixes) {
            int fromPotionId = ((Number) potionMix.get("inputId")).intValue(); // gson returns doubles...
            int fromPotionMeta = ((Number) potionMix.get("inputMeta")).intValue();
            int ingredient = ((Number) potionMix.get("reagentId")).intValue();
            int ingredientMeta = ((Number) potionMix.get("reagentMeta")).intValue();
            int toPotionId = ((Number) potionMix.get("outputId")).intValue();
            int toPotionMeta = ((Number) potionMix.get("outputMeta")).intValue();

            registerBrewingRecipe(new BrewingRecipe(Item.get(fromPotionId, fromPotionMeta), Item.get(ingredient, ingredientMeta), Item.get(toPotionId, toPotionMeta)));
        }

        List<Map> containerMixes = config.getMapList("containerMixes");

        for (Map containerMix : containerMixes) {
            int fromItemId = ((Number) containerMix.get("inputId")).intValue();
            int ingredient = ((Number) containerMix.get("reagentId")).intValue();
            int toItemId = ((Number) containerMix.get("outputId")).intValue();

            registerContainerRecipe(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
        }
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

        for (MultiRecipe recipe : this.multiRecipes.values()) {
            pk.addMultiRecipe(recipe);
        }

        for (BrewingRecipe recipe : brewingRecipes.values()) {
            pk.addBrewingRecipe(recipe);
        }

        for (ContainerRecipe recipe : containerRecipes.values()) {
            pk.addContainerRecipe(recipe);
        }

        pk.tryEncode();
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
        if (recipe == null) recipe = this.furnaceRecipes.get(getItemHash(input.getId(), 0));
        return recipe;
    }

    private static UUID getMultiItemHash(Collection<Item> items) {
        BinaryStream stream = new BinaryStream(new byte[5 * items.size()]).reset();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    private static int getFullItemHash(Item item) {
        return (getItemHash(item) << 6) | (item.getCount() & 0x3f);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        this.furnaceRecipes.put(getItemHash(input), recipe);
    }

    private static int getItemHash(Item item) {
        return getItemHash(item.getId(), item.getDamage());
    }

    private static int getItemHash(int id, int meta) {
        return (id << 12) | (meta & 0xfff);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapedRecipe> map = shapedRecipes.computeIfAbsent(resultHash, k -> new HashMap<>());
        List<Item> inputList = new LinkedList<>(recipe.getIngredientsAggregate());
        map.put(getMultiItemHash(inputList), recipe);
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
        List<Item> list = recipe.getIngredientsAggregate();

        UUID hash = getMultiItemHash(list);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map = shapelessRecipes.computeIfAbsent(resultHash, k -> new HashMap<>());

        map.put(hash, recipe);
    }

    private static int getPotionHash(Item ingredient, Item potion) {
        int ingredientHash = ((ingredient.getId() & 0x3FF) << 6) | (ingredient.getDamage() & 0x3F);
        int potionHash = ((potion.getId() & 0x3FF) << 6) | (potion.getDamage() & 0x3F);
        return ingredientHash << 16 | potionHash;
    }

    private static int getContainerHash(int ingredientId, int containerId) {
        return (ingredientId << 15) | containerId;
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        this.brewingRecipes.put(getPotionHash(input, potion), recipe);
    }

    public void registerContainerRecipe(ContainerRecipe recipe) {
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        this.containerRecipes.put(getContainerHash(input.getId(), potion.getId()), recipe);
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        int id = potion.getId();
        if (id == Item.POTION || id == Item.SPLASH_POTION || id == Item.LINGERING_POTION) {
            return this.brewingRecipes.get(getPotionHash(input, potion));
        }

        return null;
    }

    public ContainerRecipe matchContainerRecipe(Item input, Item potion) {
        return this.containerRecipes.get(getContainerHash(input.getId(), potion.getId()));
    }

    public CraftingRecipe matchRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        //TODO: try to match special recipes before anything else (first they need to be implemented!)

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

    public CampfireRecipe matchCampfireRecipe(Item input) {
        CampfireRecipe recipe = this.campfireRecipes.get(getItemHash(input));
        if (recipe == null) recipe = this.campfireRecipes.get(getItemHash(input.getId(), 0));
        return recipe;
    }

    private boolean matchItemsAccumulation(CraftingRecipe recipe, List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
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
            if (item.isTool() && item.getDamage() > 0) {
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

    public void registerMultiRecipe(MultiRecipe recipe) {
        this.multiRecipes.put(recipe.getId(), recipe);
    }

    public void registerCampfireRecipe(CampfireRecipe recipe) {
        this.campfireRecipes.put(getItemHash(recipe.getInput()), recipe);
    }

    public void registerSmithingRecipe(SmithingRecipe recipe) {
        this.smithingRecipes.put(getMultiItemHash(recipe.getIngredientsAggregate()), recipe);
    }
}
