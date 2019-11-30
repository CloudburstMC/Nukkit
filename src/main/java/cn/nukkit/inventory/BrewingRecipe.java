package cn.nukkit.inventory;


import cn.nukkit.item.Item;


public class BrewingRecipe extends MixRecipe {

    public BrewingRecipe(Item input, Item ingredient, Item output) {
        super(input, ingredient, output);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerBrewingRecipe(this);
    }

    @Override
    public RecipeType getType() {
        throw new UnsupportedOperationException();
    }
}