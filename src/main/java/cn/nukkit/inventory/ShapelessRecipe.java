package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapelessRecipe implements Recipe {

    private Item output;

    private List<Item> ingredients = new ArrayList<>();

    public ShapelessRecipe(Item result) {
        this.output = result.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    public ShapelessRecipe addIngredient(Item item) {
        if (this.ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }

        Item it = item.clone();
        it.setCount(1);

        while (item.getCount() > 0) {
            this.ingredients.add(it.clone());
            item.setCount(item.getCount() - 1);
        }

        return this;
    }

    public ShapelessRecipe removeIngredient(Item item) {
        for (Item ingredient : this.ingredients) {
            if (item.getCount() <= 0) {
                break;
            }

            if (ingredient.equals(item, item.getDamage() != null)) {
                this.ingredients.remove(ingredient);
                item.setCount(item.getCount() - 1);
            }
        }

        return this;
    }

    public Item[] getIngredientList() {
        List<Item> ingredients = new ArrayList<>();
        for (Item ingredient : this.ingredients) {
            ingredients.add(ingredient.clone());
        }

        return ingredients.stream().toArray(Item[]::new);
    }

    public int getIngredientCount() {
        int count = 0;
        for (Item ingredient : this.ingredients) {
            count += ingredient.getCount();
        }

        return count;
    }

    @Override
    public void registerToCraftingManager() {
        Server.getInstance().getCraftingManager().registerShapelessRecipe(this);
    }
}
