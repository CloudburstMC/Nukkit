package cn.nukkit.inventory.crafting.recipe;

import cn.nukkit.inventory.crafting.CraftingManager;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.CraftingData;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapelessRecipe extends CraftingRecipe {
    private final List<Item> ingredients;

    public ShapelessRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients, Identifier block) {
        super(recipeId, priority, result, block);
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
    public CraftingData toNetwork() {
        return CraftingData.fromShapeless(this.getRecipeId(), Item.toNetwork(this.getIngredientList()),
                Item.toNetwork(this.getAllResults()), this.getId(), this.getBlock().getName(), this.getPriority());
    }

    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }
}
