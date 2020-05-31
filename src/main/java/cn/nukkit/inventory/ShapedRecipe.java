package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.ImmutableList;
import com.nukkitx.protocol.bedrock.data.CraftingData;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.collection.CharObjectMap;

import java.util.*;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapedRecipe implements CraftingRecipe {

    private final String recipeId;
    private final Item primaryResult;
    private final ImmutableList<Item> extraResults;
    private final List<Item> ingredientsAggregate = new ArrayList<>();
    private final CharObjectHashMap<Item> ingredients = new CharObjectHashMap<>();
    private final String[] shape;
    private final int priority;
    private final Identifier block;

    private UUID id;

    /**
     * Constructs a ShapedRecipe instance.
     *
     * @param primaryResult    Primary result of the recipe
     * @param shape<br>        Array of 1, 2, or 3 strings representing the rows of the recipe.
     *                         This accepts an array of 1, 2 or 3 strings. Each string should be of the same length and must be at most 3
     *                         characters long. Each character represents a unique type of ingredient. Spaces are interpreted as air.
     * @param ingredients<br>  Char =&gt; Item map of items to be set into the shape.
     *                         This accepts an array of Items, indexed by character. Every unique character (except space) in the shape
     *                         array MUST have a corresponding item in this list. Space character is automatically treated as air.
     * @param extraResults<br> List of additional result items to leave in the crafting grid afterwards. Used for things like cake recipe
     *                         empty buckets.
     *                         <p>
     *                         Note: Recipes **do not** need to be square. Do NOT add padding for empty rows/columns.
     */
    public ShapedRecipe(String recipeId, int priority, Item primaryResult, String[] shape,
                        CharObjectMap<Item> ingredients, List<Item> extraResults, Identifier block) {
        this.recipeId = recipeId;
        this.priority = priority;
        int rowCount = shape.length;
        if (rowCount > 3 || rowCount <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 rows, not " + rowCount);
        }

        int columnCount = shape[0].length();
        if (columnCount > 3 || columnCount <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 columns, not " + columnCount);
        }


        //for($shape as $y => $row) {
        for (String row : shape) {
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
        this.extraResults = ImmutableList.copyOf(extraResults);
        this.block = block;
        this.shape = shape;

        for (Map.Entry<Character, Item> entry : ingredients.entrySet()) {
            this.setIngredient(entry.getKey(), entry.getValue());
        }

        for (char c : String.join("", this.shape).toCharArray()) {
            if (c == ' ')
                continue;
            Item ingredient = this.ingredients.get(c).clone();
            for (Item existingIngredient : this.ingredientsAggregate) {
                if (existingIngredient.equals(ingredient, ingredient.hasMeta(), ingredient.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + ingredient.getCount());
                    ingredient = null;
                    break;
                }
            }
            if (ingredient != null)
                this.ingredientsAggregate.add(ingredient);
        }
        this.ingredientsAggregate.sort(CraftingManager.recipeComparator);
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
    public String getRecipeId() {
        return this.recipeId;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public ShapedRecipe setIngredient(String key, Item item) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(char key, Item item) {
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

    public Item getIngredient(int x, int y) {
        Item item = this.ingredients.get(this.shape[y].charAt(x));

        return item != null ? item.clone() : Item.get(AIR);
    }

    public String[] getShape() {
        return shape;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapedRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }

    @Override
    public List<Item> getExtraResults() {
        return extraResults;
    }

    @Override
    public List<Item> getAllResults() {
        List<Item> list = new ArrayList<>();
        list.add(primaryResult);
        list.addAll(this.extraResults);

        return list;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean matchItems(List<Item> input, List<Item> output) {
        List<Item> haveInputs = new ArrayList<>(input);
        List<Item> needInputs = new ArrayList<>(ingredientsAggregate);

        if (!matchItemList(haveInputs, needInputs)) {
            return false;
        }

        List<Item> haveOutputs = new LinkedList<>(output);
        haveOutputs.sort(CraftingManager.recipeComparator);
        List<Item> needOutputs = new LinkedList<>(this.getExtraResults());
        needOutputs.sort(CraftingManager.recipeComparator);

        return this.matchItemList(haveOutputs, needOutputs);
    }

    private boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
        haveItems.removeIf(Item::isNull);
        needItems.removeIf(Item::isNull);

        if (haveItems.size() != needItems.size()) {
            return false;
        }

        int size = needItems.size();
        int completed = 0;
        for (int i = 0; i < size; i++) {
            Item haveItem = haveItems.get(i);
            Item needItem = needItems.get(i);

            if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag()) && haveItem.getCount() == needItem.getCount()) {
                completed++;
            }
        }

        return completed == size;
    }

    @Override
    public Identifier getBlock() {
        return block;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");

        ingredients.forEach((character, item) -> joiner.add(item.getName() + ":" + item.getMeta()));
        return joiner.toString();
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.getHeight() > 2 || this.getWidth() > 2;
    }

    @Override
    public CraftingData toNetwork() {
        return CraftingData.fromShaped(this.recipeId, this.getWidth(), this.getHeight(),
                Item.toNetwork(this.getIngredientList()), Item.toNetwork(this.getAllResults()), this.getId(),
                this.block.getName(), this.priority);
    }

    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }
}
