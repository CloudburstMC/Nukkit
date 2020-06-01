package cn.nukkit.inventory.crafting.recipe;

import cn.nukkit.inventory.crafting.CraftingManager;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

import java.util.*;

/**
 * @author CreeperFace
 */
public abstract class CraftingRecipe implements Recipe {
    private UUID id;
    private final String recipeId;
    protected final Item primaryResult;
    protected final List<Item> ingredientsAggregate = new ArrayList<>();
    private final int priority;
    private final Identifier block;

    public CraftingRecipe(String recipeId, int priority, Item primaryResult, Identifier block) {
        this.recipeId = recipeId;
        this.priority = priority;
        this.primaryResult = primaryResult;
        this.block = block;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Item> getExtraResults(){
        return Collections.emptyList();
    }

    public List<Item> getAllResults(){
        return Collections.singletonList(primaryResult);
    }

    @Override
    public Item getResult() {
        return this.primaryResult.clone();
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public Identifier getBlock() {
        return block;
    }

    public boolean requiresCraftingTable() {
        return true;
    }

    /**
     * Returns whether the specified list of crafting grid inputs and outputs matches this recipe. Outputs DO NOT
     * include the primary result item.
     *
     * @param inputList  list of items taken from the crafting grid
     * @param extraOutputList list of items put back into the crafting grid (secondary results)
     * @return bool
     */
    public boolean matchItems(List<Item> inputList, List<Item> extraOutputList) {
        List<Item> haveInputs = new ArrayList<>();
        for (Item item : inputList) {
            if (item.isNull())
                continue;
            haveInputs.add(item.clone());
        }
        List<Item> needInputs = new ArrayList<>();
        for (Item item : ingredientsAggregate) {
            if (item.isNull())
                continue;
            needInputs.add(item.clone());
        }

        if (!matchItemList(haveInputs, needInputs)) {
            return false;
        }

        List<Item> haveOutputs = new ArrayList<>();
        for (Item item : extraOutputList) {
            if (item.isNull())
                continue;
            haveOutputs.add(item.clone());
        }
        haveOutputs.sort(CraftingManager.recipeComparator);
        List<Item> needOutputs = new ArrayList<>();
        for (Item item : this.getExtraResults()) {
            if (item.isNull())
                continue;
            needOutputs.add(item.clone());
        }
        needOutputs.sort(CraftingManager.recipeComparator);

        return this.matchItemList(haveOutputs, needOutputs);
    }

    private boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
        for (Item needItem : new ArrayList<>(needItems)) {
            for (Item haveItem : new ArrayList<>(haveItems)) {
                if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag())) {
                    int amount = Math.min(haveItem.getCount(), needItem.getCount());
                    needItem.setCount(needItem.getCount() - amount);
                    haveItem.setCount(haveItem.getCount() - amount);
                    if (haveItem.getCount() == 0) {
                        haveItems.remove(haveItem);
                    }
                    if (needItem.getCount() == 0) {
                        needItems.remove(needItem);
                        break;
                    }
                }
            }
        }
        return haveItems.isEmpty() && needItems.isEmpty();
    }
}
