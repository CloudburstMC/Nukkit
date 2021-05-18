/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.nukkit.inventory.Recipe.matchItemList;

/**
 * @author joserobjr
 * @since 2020-09-28
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
public class SmithingRecipe implements Recipe {
    private final Item equipment;
    private final Item ingredient;
    private final Item result;

    private final List<Item> ingredientsAggregate;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmithingRecipe(Item equipment, Item ingredient, Item result) {
        this.equipment = equipment;
        this.ingredient = ingredient;
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
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getFinalResult(Item equip) {
        Item finalResult = getResult().clone();
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
        return RecipeType.SMITHING;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getEquipment() {
        return equipment;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getIngredient() {
        return ingredient;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean matchItems(List<Item> inputList) {
        return matchItems(inputList, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
}
