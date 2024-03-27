package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author joserobjr
 */
@ToString
public class SmithingRecipe extends ShapelessRecipe {
    private final Item equipment;
    private final Item ingredient;
    private final Item result;

    private final List<Item> ingredientsAggregate;

    public SmithingRecipe(String recipeId, int priority, Collection<Item> ingredients, Item result) {
        super(recipeId, priority, result, ingredients);
        this.equipment = (Item) ingredients.toArray()[0];
        this.ingredient = (Item) ingredients.toArray()[1];
        this.result = result;

        ArrayList<Item> aggregation = new ArrayList<>(2);

        for (Item item : new Item[]{equipment, ingredient}) {
            if (item.getCount() < 1) {
                throw new IllegalArgumentException("Recipe Ingredient amount was not 1 (value: " + item.getCount() + ")");
            }
            boolean found = false;
            for (Item existingIngredient : aggregation) {
                if (existingIngredient.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + item.getCount());
                    found = true;
                    break;
                }
            }
            if (!found) {
                aggregation.add(item.clone());
            }
        }

        aggregation.trimToSize();
        aggregation.sort(CraftingManager.recipeComparator);
        this.ingredientsAggregate = Collections.unmodifiableList(aggregation);
    }

    @Override
    public Item getResult() {
        return result;
    }

    public Item getFinalResult(Item equip) {
        Item finalResult = getResult().clone();

        if (equip.hasCompoundTag()) {
            finalResult.setCompoundTag(equip.getCompoundTag());
        }

        int maxDurability = finalResult.getMaxDurability();
        if (maxDurability <= 0 || equip.getMaxDurability() <= 0) {
            return finalResult;
        }

        int damage = equip.getDamage();
        if (damage <= 0) {
            return finalResult;
        }

        finalResult.setDamage(Math.min(maxDurability, damage));
        return finalResult;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmithingRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRANSFORM;
    }

    public Item getEquipment() {
        return equipment;
    }

    public Item getIngredient() {
        return ingredient;
    }

    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }

    public boolean matchItems(List<Item> inputList) {
        return matchItems(inputList, 1);
    }

    public boolean matchItems(List<Item> inputList, int multiplier) {
        List<Item> haveInputs = new ArrayList<>();
        for (Item item : inputList) {
            if (item.isNull())
                continue;
            haveInputs.add(item.clone());
        }
        List<Item> needInputs = new ArrayList<>();
        if(multiplier != 1){
            for (Item item : ingredientsAggregate) {
                if (item.isNull())
                    continue;
                Item itemClone = item.clone();
                itemClone.setCount(itemClone.getCount() * multiplier);
                needInputs.add(itemClone);
            }
        } else {
            for (Item item : ingredientsAggregate) {
                if (item.isNull())
                    continue;
                needInputs.add(item.clone());
            }
        }

        return matchItemList(haveInputs, needInputs);
    }

    private static boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
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
