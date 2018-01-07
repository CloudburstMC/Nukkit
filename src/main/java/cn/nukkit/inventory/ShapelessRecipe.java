package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShapelessRecipe implements CraftingRecipe {

    private final Item output;

    private UUID uuid = null;

    private final List<Item> ingredients = new ArrayList<>();

    private int recipeProtocol = 130;

    public ShapelessRecipe(Item result) {
        this.output = result.clone();
    }

    @Override
    public boolean isCompatibleWith(int protocolVersion) {
        return recipeProtocol <= protocolVersion;
    }

    @Override
    public void setRecipeProtocol(int protocol){
        this.recipeProtocol = protocol;
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    @Override
    public void setId(UUID uuid) {
        if (this.uuid != null) {
            throw new IllegalStateException("Id is already set");
        }
        this.uuid = uuid;
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

            if (ingredient.equals(item, item.hasMeta(), item.getCompoundTag() != null)) {
                this.ingredients.remove(ingredient);
                item.setCount(item.getCount() - 1);
            }
        }

        return this;
    }

    public List<Item> getIngredientList() {
        List<Item> ingredients = new ArrayList<>();
        for (Item ingredient : this.ingredients) {
            ingredients.add(ingredient.clone());
        }

        return ingredients;
    }

    public int getIngredientCount() {
        int count = 0;
        for (Item ingredient : this.ingredients) {
            count += ingredient.getCount();
        }

        return count;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapelessRecipe(this);
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.ingredients.size() > 4;
    }

    @Override
    public List<Item> getExtraResults() {
        return null;
    }

    @Override
    public List<Item> getAllResults() {
        return null;
    }

    @Override
    public boolean matchItems(Item[][] input, Item[][] output) {
        List<Item> haveInputs = new ArrayList<>();
        for (Item[] items : input) {
            haveInputs.addAll(Arrays.asList(items));
        }

        List<Item> needInputs = this.getIngredientList();

        if (!this.matchItemList(haveInputs, needInputs)) {
            return false;
        }

        List<Item> haveOutputs = new ArrayList<>();
        for (Item[] items : input) {
            haveOutputs.addAll(Arrays.asList(items));
        }
        List<Item> needOutputs = this.getExtraResults();

        return this.matchItemList(haveOutputs, needOutputs);
    }


    private boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
        for (Item haveItem : new ArrayList<>(haveItems)) {
            if (haveItem.isNull()) {
                haveItems.remove(haveItem);
                continue;
            }


            for (Item needItem : new ArrayList<>(needItems)) {
                if (needItem.equals(haveItem, !needItem.hasAnyDamageValue(), needItem.hasCompoundTag()) && needItem.getCount() == haveItem.getCount()) {
                    haveItems.remove(haveItem);
                    needItems.remove(needItem);
                    break;
                }
            }
        }

        return haveItems.isEmpty() && haveItems.isEmpty();
    }
}
