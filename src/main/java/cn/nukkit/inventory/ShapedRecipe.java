package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapedRecipe implements Recipe {

    private Item output;

    private String[] rows = new String[3];

    private Map<Character, Item> ingredients = new HashMap<>();

    public ShapedRecipe(Item result, String[] shape) {
        if (shape.length == 0) {
            throw new IllegalArgumentException("Must provide a shape");
        }

        if (shape.length > 3) {
            throw new IllegalStateException("Crafting recipes should be 1, 2, 3 rows, not " + shape.length);
        }

        for (int i = 0; i < 3; i++) {
            String row = shape[i];
            if (row.length() == 0 || row.length() > 3) {
                throw new IllegalStateException("Crafting rows should be 1, 2, 3 characters, not " + row.length());
            }

            this.rows[i] = row;
            int len = row.length();
            for (int j = 0; j < len; j++) {
                this.ingredients.put(row.charAt(j), null);
            }
        }
        this.output = result.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    public ShapedRecipe setIngredient(char key, Item item) {
        if (this.ingredients.size() > 9) {
            throw new RuntimeException("Symbol does not appear in the shape: " + key);
        }

        this.ingredients.put(key, item);

        return this;
    }

    public Map<Character, Item> getIngredientMap() {
        Map<Character, Item> ingredients = new HashMap<>();
        for (Map.Entry<Character, Item> entry : this.ingredients.entrySet()) {
            char key = entry.getKey();
            Item ingredient = entry.getValue();
            if (ingredient != null) {
                ingredients.put(key, ingredient.clone());
            } else {
                //todo: check this
                ingredients.put(key, ingredient);
            }
        }

        return ingredients;
    }

    public String[] getShape() {
        return rows;
    }

    @Override
    public void registerToCraftingManager() {
        Server.getInstance().getCraftingManager().registerShapdRecipe(this);
    }
}
