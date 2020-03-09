package cn.nukkit.inventory;

import cn.nukkit.blockentity.Furnace;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceInventory extends ContainerInventory {

    public FurnaceInventory(Furnace furnace, InventoryType inventoryType) {
        super(furnace, inventoryType);
    }

    @Override
    public Furnace getHolder() {
        return (Furnace) this.holder;
    }

    public Item getResult() {
        return this.getItem(2);
    }

    public Item getFuel() {
        return this.getItem(1);
    }

    public Item getSmelting() {
        return this.getItem(0);
    }

    public boolean setResult(Item item) {
        return this.setItem(2, item);
    }

    public boolean setFuel(Item item) {
        return this.setItem(1, item);
    }

    public boolean setSmelting(Item item) {
        return this.setItem(0, item);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().scheduleUpdate();
    }
}
