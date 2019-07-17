package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapelessRecipe implements CraftingRecipe {

    private String recipeId;

    private final Item output;

    private long least,most;

    private final List<Item> ingredients;

    private final int priority;

    public ShapelessRecipe(Item result, Collection<Item> ingredients) {
        this(null, 10, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        this.recipeId = recipeId;
        this.priority = priority;
        this.output = result.clone();
        if (ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }

        this.ingredients = new ArrayList<>();

        for (Item item : ingredients) {
            if (item.getCount() < 1) {
                throw new IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + item.getCount() + ")");
            }
            this.ingredients.add(item.clone());
        }
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public String getRecipeId() {
        return this.recipeId;
    }

    @Override
    public UUID getId() {
        return new UUID(least, most);
    }

    @Override
    public void setId(UUID uuid) {
        this.least = uuid.getLeastSignificantBits();
        this.most = uuid.getMostSignificantBits();

        if (this.recipeId == null) {
            this.recipeId = this.getId().toString();
        }
    }

    public List<Item> getIngredientList() {
        List<Item> ingredients = new ArrayList<>();
        for (Item ingredient : this.ingredients) {
            ingredients.add(ingredient.clone());
        }

        return ingredients;
    }

    public int getIngredientCount() {
        return ingredients.size();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapelessRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.ingredients.size() > 4;
    }

    @Override
    public List<Item> getExtraResults() {
        return new ArrayList<>();
    }

    @Override
    public List<Item> getAllResults() {
        return null;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean matchItems(Item[][] input, Item[][] output) {
        List<Item> haveInputs = new ArrayList<>();
        for (Item[] items : input) {
            haveInputs.addAll(Arrays.asList(items));
        }
        haveInputs.sort(CraftingManager.recipeComparator);

        List<Item> needInputs = this.getIngredientList();

        if (!this.matchItemList(haveInputs, needInputs)) {
            return false;
        }

        List<Item> haveOutputs = new ArrayList<>();
        for (Item[] items : output) {
            haveOutputs.addAll(Arrays.asList(items));
        }
        haveOutputs.sort(CraftingManager.recipeComparator);
        List<Item> needOutputs = this.getExtraResults();

        return this.matchItemList(haveOutputs, needOutputs);
    }


    private boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
        // Remove any air blocks that may have gotten through.
        haveItems.removeIf(Item::isNull);

        if (haveItems.size() != needItems.size()) {
            return false;
        }

        int size = needItems.size();
        int completed = 0;
        for (int i = 0; i < size; i++) {
            Item haveItem = haveItems.get(i);
            Item needItem = needItems.get(i);

            if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag())) {
                completed++;
            }
        }

        return completed == size;
    }
}
