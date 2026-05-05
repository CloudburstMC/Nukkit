package cn.nukkit.inventory;

import cn.nukkit.item.Item;

public class BlastFurnaceRecipe extends FurnaceRecipe {

    public BlastFurnaceRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId, result, ingredient);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerBlastFurnaceRecipe(this);
    }
}
