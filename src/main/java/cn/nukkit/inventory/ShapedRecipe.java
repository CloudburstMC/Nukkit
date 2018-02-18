package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.PlayerProtocol;
import cn.nukkit.utils.Utils;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapedRecipe implements CraftingRecipe {

    private Item primaryResult;
    private List<Item> extraResults = new ArrayList<>();

    private long least,most;

    private final String[] shape;

    private final Map<Character, Item> ingredients = new HashMap<>();

    private int recipeProtocol = PlayerProtocol.getNewestProtocol().getNumber();

    /**
     * Constructs a ShapedRecipe instance.
     *
     * @param primaryResult
     * @param shape<br>        Array of 1, 2, or 3 strings representing the rows of the recipe.
     *                         This accepts an array of 1, 2 or 3 strings. Each string should be of the same length and must be at most 3
     *                         characters long. Each character represents a unique type of ingredient. Spaces are interpreted as air.
     * @param ingredients<br>  Char => Item map of items to be set into the shape.
     *                         This accepts an array of Items, indexed by character. Every unique character (except space) in the shape
     *                         array MUST have a corresponding item in this list. Space character is automatically treated as air.
     * @param extraResults<br> List of additional result items to leave in the crafting grid afterwards. Used for things like cake recipe
     *                         empty buckets.
     *                         <p>
     *                         Note: Recipes **do not** need to be square. Do NOT add padding for empty rows/columns.
     */
    public ShapedRecipe(Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        int rowCount = shape.length;
        if (rowCount > 3 || rowCount <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 rows, not " + rowCount);
        }

        int columnCount = shape[0].length();
        if (columnCount > 3 || rowCount <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 columns, not " + columnCount);
        }


        //for($shape as $y => $row) {
        for (int y = 0; y < rowCount; y++) {
            String row = shape[y];

            if (row.length() != columnCount) {
                throw new RuntimeException("Shaped recipe rows must all have the same length (expected " + columnCount + ", got " + row.length() + ")");
            }

            for (int x = 0; x < columnCount; ++x) {
                char c = row.charAt(x);

                if (c != ' ' && !ingredients.containsKey(c)) {
                    throw new RuntimeException("No item specified for symbol '" + c + "'");
                }
            }
        }

        this.primaryResult = primaryResult.clone();
        this.extraResults.addAll(extraResults);

        this.shape = shape;

        for (Map.Entry<Character, Item> entry : ingredients.entrySet()) {
            this.setIngredient(entry.getKey(), entry.getValue());
        }
    }

    public int getWidth() {
        return this.shape[0].length();
    }

    public int getHeight() {
        return this.shape.length;
    }

    @Override
    public Item getResult() {
        return this.primaryResult;
    }

    @Override
    public UUID getId() {
        return new UUID(least, most);
    }

    @Override
    public void setId(UUID uuid) {
        this.least = uuid.getLeastSignificantBits();
        this.most = uuid.getMostSignificantBits();
    }

    @Override
    public boolean isCompatibleWith(int protocolVersion){
        return recipeProtocol <= protocolVersion;
    }
    @Override
    public void setRecipeProtocol(int protocol){
        this.recipeProtocol = protocol;
    }

    public ShapedRecipe setIngredient(String key, Item item) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(char key, Item item) {
        if (String.join("", this.shape).indexOf(key) < 0) {
            throw new RuntimeException("Symbol does not appear in the shape: " + key);
        }

        this.ingredients.put(key, item);
        return this;
    }

    public List<Item> getIngredientList() {
        List<Item> items = new ArrayList<>();
        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                items.add(getIngredient(x, y));
            }
        }
        return items;
    }

    public Map<Integer, Map<Integer, Item>> getIngredientMap() {
        Map<Integer, Map<Integer, Item>> ingredients = new LinkedHashMap<>();

        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            Map<Integer, Item> m = new LinkedHashMap<>();

            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                m.put(x, getIngredient(x, y));
            }

            ingredients.put(y, m);
        }

        return ingredients;
    }

    public Item getIngredient(int x, int y) {
        Item item = this.ingredients.get(this.shape[y].charAt(x));

        return item != null ? item.clone() : Item.get(Item.AIR);
    }

    public String[] getShape() {
        return shape;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapedRecipe(this);
    }

    @Override
    public List<Item> getExtraResults() {
        return extraResults;
    }

    @Override
    public List<Item> getAllResults() {
        List<Item> list = new ArrayList<>(this.extraResults);
        list.add(primaryResult);

        return list;
    }

    @Override
    public boolean matchItems(Item[][] input, Item[][] output) {
        if (!matchInputMap(cloneItemArray(input))) {
            //Item[][] result = input;
            /*for(int i = 0; i < input.length; i++) {
                Item[] origin = input[i];
                Item[] dest = new Item[origin.length];

                System.arraycopy(origin, 0, dest, 0, dest.length);
                result[i] = dest;
            }*/

            for (int i = 0; i < input.length; i++) {
                Item[] old = input[i];
                Item[] newArray = new Item[old.length];
                System.arraycopy(old, 0, newArray, 0, newArray.length);
                Utils.reverseArray(newArray);

                input[i] = newArray;
            }

            if (!matchInputMap(input)) {
                return false;
            }
        }

        //and then, finally, check that the output items are good:
        List<Item> haveItems = new ArrayList<>();
        for (Item[] items : output) {
            haveItems.addAll(Arrays.asList(items));
        }

        List<Item> needItems = this.getExtraResults();

        for (Item haveItem : new ArrayList<>(haveItems)) {
            if (haveItem.isNull()) {
                haveItems.remove(haveItem);
                continue;
            }

            for (Item needItem : new ArrayList<>(needItems)) {
                if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag()) && needItem.getCount() == haveItem.getCount()) {
                    haveItems.remove(haveItem);
                    needItems.remove(needItem);
                    break;
                }
            }
        }

        return haveItems.isEmpty() && needItems.isEmpty();
    }

    private boolean matchInputMap(Item[][] input) {
        Map<Integer, Map<Integer, Item>> map = this.getIngredientMap();

        //match the given items to the requested items
        for (int y = 0, y2 = this.getHeight(); y < y2; ++y) {
            for (int x = 0, x2 = this.getWidth(); x < x2; ++x) {
                Item given = input[y][x];
                Item required = map.get(y).get(x);

                if (given == null || !required.equals(given, required.hasMeta(), required.hasCompoundTag()) || required.getCount() != given.getCount()) {
                    return false;
                }

                input[y][x] = null;
            }
        }

        //check if there are any items left in the grid outside of the recipe
        for (Item[] items : input) {
            for (Item item : items) {
                if (item != null && !item.isNull()) {
                    return false;
                }
            }
        }

        return true;
    }

    private Item[][] cloneItemArray(Item[][] map) {
        Item[][] newMap = new Item[map.length][];
        for (int i = 0; i < newMap.length; i++) {
            Item[] old = map[i];
            Item[] n = new Item[old.length];

            System.arraycopy(old, 0, n, 0, n.length);
            newMap[i] = n;
        }

        return newMap;
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.getHeight() > 2 || this.getWidth() > 2;
    }

    public static class Entry {
        public final int x;
        public final int y;

        public Entry(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
