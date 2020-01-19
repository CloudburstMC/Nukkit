package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RepairRecipe implements Recipe {
    
    private final Item result;
    private final List<Item> ingredients;
    private final InventoryType inventoryType;
    
    public RepairRecipe(InventoryType inventoryType, Item result, Collection<Item> ingredients) {
        this.inventoryType = inventoryType;
        this.result = result.clone();
        this.ingredients = new ArrayList<>();
    
        for (Item item : ingredients) {
            if (item.getCount() < 1) {
                throw new IllegalArgumentException("Recipe Ingredient amount was not 1 (value: " + item.getCount() + ")");
            }
            this.ingredients.add(item.clone());
        }
    }
    
    @Override
    public Item getResult() {
        return result.clone();
    }
    
    public List<Item> getIngredientList() {
        List<Item> ingredients = new ArrayList<>();
        for (Item ingredient : this.ingredients) {
            ingredients.add(ingredient.clone());
        }
        
        return ingredients;
    }
    
    @Override
    public void registerToCraftingManager(CraftingManager manager) {
    
    }
    
    @Override
    public RecipeType getType() {
        return RecipeType.REPAIR;
    }
    
    public InventoryType getInventoryType() {
        return inventoryType;
    }
}
