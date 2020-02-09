package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceRecipe implements Recipe {

    private final Item output;

    private Item ingredient;

    private final Identifier craftingBlock;

    public FurnaceRecipe(Item result, Item ingredient, Identifier blockId) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.craftingBlock = blockId;
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

    public Identifier getCraftingBlock() {
        return this.craftingBlock;
    }
}
