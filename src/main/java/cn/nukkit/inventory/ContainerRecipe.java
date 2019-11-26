package cn.nukkit.inventory;

import cn.nukkit.item.Item;

public class ContainerRecipe extends MixRecipe {
    public ContainerRecipe(Item input, Item ingredient, Item output) {
        super(input, ingredient, output);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerContainerRecipe(this);
    }

    @Override
    public RecipeType getType() {
        throw new UnsupportedOperationException();
    }
}
