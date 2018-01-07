package cn.nukkit.inventory;

import cn.nukkit.item.Item;

public class BrewingRecipe implements Recipe {

    private final Item output;

    private final Item potion;

    private Item ingredient;

    private int recipeProtocol = 130;

    public BrewingRecipe(Item result, Item ingredient, Item potion) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.potion = potion.clone();
    }

    @Override
    public boolean isCompatibleWith(int protocolVersion) {
        return recipeProtocol <= protocolVersion;
    }

    @Override
    public void setRecipeProtocol(int protocol){
        this.recipeProtocol = protocol;
    }

    public void setInput(Item item) {
        ingredient = item.clone();
    }

    public Item getInput() {
        return ingredient.clone();
    }

    public Item getPotion() {
        return potion.clone();
    }

    @Override
    public Item getResult() {
        return output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerBrewingRecipe(this);
    }

}