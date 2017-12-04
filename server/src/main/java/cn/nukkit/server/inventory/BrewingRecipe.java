package cn.nukkit.server.inventory;


import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.item.Item;

import java.util.UUID;

public class BrewingRecipe implements Recipe {
    private UUID id;

    private final Item output;

    private final Item potion;

    private Item ingredient;

    public BrewingRecipe(Item result, Item ingredient, Item potion) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.potion = potion.clone();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID uuid) {
        if (id != null) {
            throw new IllegalStateException("Id is already set");
        }

        this.id = uuid;
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
    public void registerToCraftingManager() {
        NukkitServer.getInstance().getCraftingManager().registerBrewingRecipe(this);
    }
}