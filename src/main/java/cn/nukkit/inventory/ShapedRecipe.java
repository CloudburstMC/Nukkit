package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.Item;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapedRecipe implements Recipe {

    private Item output;

    private UUID uuid = null;

    private Map<Character, String> shapes = new HashMap<>();

    private List<List<Item>> ingredients = new ArrayList<>();

    private Map<Character, List<int[]>> shapeItems = new HashMap<>();

    public ShapedRecipe(Item result, String... shape) {
        if (shape.length == 0) {
            throw new IllegalArgumentException("Must provide a shape");
        }

        if (shape.length > 3) {
            throw new IllegalStateException("Crafting recipes should be 1, 2, 3 rows, not " + shape.length);
        }


        for (int y = 0; y < shape.length; y++) {
            String row = shape[y];
            if (row.length() == 0 || row.length() > 3) {
                throw new IllegalStateException("Crafting rows should be 1, 2, 3 characters, not " + row.length());
            }

            this.ingredients.add(new ArrayList<>(Arrays.asList(new Item[row.length()])));
            int len = row.length();
            for (int i = 0; i < len; i++) {
                this.shapes.put(row.charAt(i), null);

                if (!this.shapeItems.containsKey(row.charAt(i))) {
                    this.shapeItems.put(row.charAt(i), new ArrayList<>(Arrays.asList(new int[][]{new int[]{i, y}})));
                } else {
                    this.shapeItems.get(row.charAt(i)).add(new int[]{i, y});
                }
            }
        }

        this.output = result.clone();
    }

    public int getWidth() {
        return this.ingredients.get(0).size();
    }

    public int getHeight() {
        return this.ingredients.size();
    }

    @Override
    public Item getResult() {
        return this.output;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void setId(UUID id) {
        if (this.uuid != null) {
            throw new IllegalStateException("Id is already set");
        }
        this.uuid = id;
    }

    public ShapedRecipe setIngredient(String key, Item item) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(char key, Item item) {
        if (!this.shapes.containsKey(key)) {
            throw new RuntimeException("Symbol does not appear in the shape: " + key);
        }

        this.fixRecipe(key, item);

        return this;
    }

    protected void fixRecipe(char key, Item item) {
        for (int[] entry : this.shapeItems.get(key)) {
            this.ingredients.get(entry[1]).add(entry[0], item.clone());
        }
    }

    public Map<Integer, Map<Integer, Item>> getIngredientMap() {
        Map<Integer, Map<Integer, Item>> ingredients = new HashMap<>();
        for (int y = 0; y < this.ingredients.size(); y++) {
            List<Item> row = this.ingredients.get(y);
            for (int x = 0; x < row.size(); x++) {
                Item ingredient = row.get(x);
                if (ingredient != null) {
                    if (!ingredients.containsKey(y)) {
                        ingredients.put(y, new HashMap<>());
                    }
                    ingredients.get(y).put(x, ingredient.clone());
                } else {
                    if (!ingredients.containsKey(y)) {
                        ingredients.put(y, new HashMap<>());
                    }
                    ingredients.get(y).put(x, Item.get(Item.AIR));
                }
            }

        }

        return ingredients;
    }

    public Item getIngredient(int x, int y) {
        try {
            return this.ingredients.get(y).get(x);
        } catch (Exception e) {
            return Item.get(Item.AIR);
        }
    }

    public Map<Character, String> getShape() {
        return shapes;
    }

    @Override
    public void registerToCraftingManager() {
        Server.getInstance().getCraftingManager().registerShapedRecipe(this);
    }
}
