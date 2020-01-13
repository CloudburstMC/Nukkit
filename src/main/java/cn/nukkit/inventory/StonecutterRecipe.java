package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.UUID;

@ToString
public class StonecutterRecipe implements Recipe {

    private String recipeId;

    private final Item output;

    private long least,most;

    private final Item ingredient;

    private final int priority;

    public StonecutterRecipe(Item result, Item ingredient) {
        this(null, 10, result, ingredient);
    }

    public StonecutterRecipe(String recipeId, int priority, Item result, Item ingredient) {
        this.recipeId = recipeId;
        this.priority = priority;
        this.output = result.clone();
        if (ingredient.getCount() < 1) {
            throw new IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + ingredient.getCount() + ")");
        }
        this.ingredient = ingredient.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public UUID getId() {
        return new UUID(least, most);
    }

    public void setId(UUID uuid) {
        this.least = uuid.getLeastSignificantBits();
        this.most = uuid.getMostSignificantBits();

        if (this.recipeId == null) {
            this.recipeId = this.getId().toString();
        }
    }

    public Item getIngredient() {
        return ingredient.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerStonecutterRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.STONECUTTER;
    }

    public int getPriority() {
        return this.priority;
    }
}
