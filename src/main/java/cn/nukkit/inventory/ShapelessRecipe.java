package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.CraftingData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapelessRecipe implements CraftingRecipe {

    private final String recipeId;
    private final Item output;
    private final List<Item> ingredients;
    private final List<Item> ingredientsAggregate = new ArrayList<>();
    private final int priority;
    private final Identifier block;

    private UUID id;

    public ShapelessRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients, Identifier block) {
        this.recipeId = recipeId;
        this.priority = priority;
        this.output = result.clone();
        this.block = block;
        if (ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }

        this.ingredients = new ArrayList<>();

        for (Item item : ingredients) {
            if (item.getCount() < 1) {
                throw new IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + item.getCount() + ")");
            }
            boolean found = false;
            for (Item existingIngredient : this.ingredientsAggregate) {
                if (existingIngredient.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + item.getCount());
                    found = true;
                    break;
                }
            }
            if (!found)
                this.ingredientsAggregate.add(item.clone());
            this.ingredients.add(item.clone());
        }

        this.ingredientsAggregate.sort(CraftingManager.recipeComparator);
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
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
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
        return Collections.singletonList(this.getResult());
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean matchItems(List<Item> input, List<Item> output) {
        List<Item> haveInputs = input.stream().map(Item::clone).collect(Collectors.toList());
        List<Item> needInputs = ingredientsAggregate.stream().map(Item::clone).collect(Collectors.toList());

        if (!matchItemList(haveInputs, needInputs)) {
            return false;
        }

        List<Item> haveOutputs = output.stream().map(Item::clone).sorted(CraftingManager.recipeComparator).collect(Collectors.toList());
        List<Item> needOutputs = this.getExtraResults().stream().map(Item::clone).sorted(CraftingManager.recipeComparator).collect(Collectors.toList());

        return this.matchItemList(haveOutputs, needOutputs);
    }

    private boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
        haveItems.removeIf(Item::isNull);
        needItems.removeIf(Item::isNull);

        for (Item needItem : new ArrayList<>(needItems)) {
            for (Item haveItem : new ArrayList<>(haveItems)) {
                if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag())) {
                    int amount = Math.min(haveItem.getCount(), needItem.getCount());
                    needItem.setCount(needItem.getCount() - amount);
                    haveItem.setCount(haveItem.getCount() - amount);
                    if (haveItem.getCount() == 0) {
                        haveItems.remove(haveItem);
                    }
                    if (needItem.getCount() == 0) {
                        needItems.remove(needItem);
                        break;
                    }
                }
            }
        }

        return haveItems.isEmpty() && needItems.isEmpty();
    }

    @Override
    public Identifier getBlock() {
        return block;
    }

    @Override
    public CraftingData toNetwork() {
        return CraftingData.fromShapeless(this.recipeId, Item.toNetwork(this.getIngredientList()),
                Item.toNetwork(this.getAllResults()), this.id, this.block.getName(), this.priority);
    }

    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }
}
