package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.PlayerProtocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceRecipe implements Recipe {

    private final Item output;

    private Item ingredient;

    private int recipeProtocol = PlayerProtocol.getNewestProtocol().getNumber();

    public FurnaceRecipe(Item result, Item ingredient) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
    }

    @Override
    public boolean isCompatibleWith(int protocolVersion){
        return recipeProtocol <= protocolVersion;
    }
    @Override
    public void setRecipeProtocol(int protocol){
        this.recipeProtocol = protocol;
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
}
