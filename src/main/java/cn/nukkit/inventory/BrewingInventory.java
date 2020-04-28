package cn.nukkit.inventory;


import cn.nukkit.blockentity.BrewingStand;
import cn.nukkit.item.Item;

public class BrewingInventory extends ContainerInventory {

    public static final int SLOT_INGREDIENT = 0;
    public static final int SLOT_FUEL = 4;

    public BrewingInventory(BrewingStand brewingStand) {
        super(brewingStand, InventoryType.BREWING_STAND);
    }

    @Override
    public BrewingStand getHolder() {
        return (BrewingStand) this.holder;
    }

    public Item getIngredient() {
        return getItem(SLOT_INGREDIENT);
    }

    public void setIngredient(Item item) {
        setItem(SLOT_INGREDIENT, item);
    }

    public void setFuel(Item fuel) {
        setItem(SLOT_FUEL, fuel);
    }

    public Item getFuel() {
        return getItem(SLOT_FUEL);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().scheduleUpdate();
    }
}