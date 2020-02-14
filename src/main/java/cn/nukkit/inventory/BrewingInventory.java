package cn.nukkit.inventory;


import cn.nukkit.blockentity.BrewingStand;
import cn.nukkit.item.Item;

public class BrewingInventory extends ContainerInventory {
    public BrewingInventory(BrewingStand brewingStand) {
        super(brewingStand, InventoryType.BREWING_STAND);
    }

    @Override
    public BrewingStand getHolder() {
        return (BrewingStand) this.holder;
    }

    public Item getIngredient() {
        return getItem(0);
    }

    public void setIngredient(Item item) {
        setItem(0, item);
    }

    public void setFuel(Item fuel) {
        setItem(4, fuel);
    }

    public Item getFuel() {
        return getItem(4);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().scheduleUpdate();
    }
}