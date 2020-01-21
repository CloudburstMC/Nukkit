package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.Collection;

public class CartographyRecipe extends ShapelessRecipe {
    public CartographyRecipe(Item result, Collection<Item> ingredients) {
        super(result, ingredients);
    }
    
    public CartographyRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        super(recipeId, priority, result, ingredients);
    }
    
    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerCartographyRecipe(this);
    }
    
    @Override
    public RecipeType getType() {
        return RecipeType.CARTOGRAPHY;
    }
}
