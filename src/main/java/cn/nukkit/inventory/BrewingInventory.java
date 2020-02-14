package cn.nukkit.inventory;


import cn.nukkit.blockentity.impl.BrewingStandBlockEntity;
import cn.nukkit.item.Item;

public class BrewingInventory extends ContainerInventory {
    public BrewingInventory(BrewingStandBlockEntity brewingStand) {
        super(brewingStand, InventoryType.BREWING_STAND);
    }

    @Override
    public BrewingStandBlockEntity getHolder() {
        return (BrewingStandBlockEntity) this.holder;
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

        if (index >= 1 && index <= 3) {
            this.getHolder().updateBlock();
        }

        this.getHolder().scheduleUpdate();
    }
}