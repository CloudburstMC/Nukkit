package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.Getter;

import java.util.UUID;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class FurnaceRecipe implements Recipe {

    private final Item output;

    private Item ingredient;

    @Getter
    private String recipeId;

    @Getter
    private UUID id;

    @Getter
    private final int networkId;

    @Deprecated
    public FurnaceRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public FurnaceRecipe(String recipeId, Item result, Item ingredient) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.recipeId = recipeId;
        this.networkId = ++CraftingManager.NEXT_NETWORK_ID;
    }

    public void setInput(Item item) {
        this.ingredient = item.clone();
    }

    public Item getInput() {
        return this.ingredient.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerFurnaceRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.FURNACE_DATA : RecipeType.FURNACE;
    }

    public void setId(UUID id) {
        this.id = id;

        if (this.recipeId == null) {
            this.recipeId = this.getId().toString();
        }
    }
}
