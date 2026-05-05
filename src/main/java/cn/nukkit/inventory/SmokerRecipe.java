package cn.nukkit.inventory;

import cn.nukkit.item.Item;

public class SmokerRecipe extends FurnaceRecipe {

    public SmokerRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId, result, ingredient);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmokerRecipe(this);
    }
}
