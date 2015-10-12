package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceRecipe implements Recipe {

    private Item output;

    private Item ingredient;

    public FurnaceRecipe(Item result, Item ingredient) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
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
    public void registerToCraftingManager() {
        Server.getInstance().getCraftingManager().registerFurnaceRecipe(this);
    }
}
