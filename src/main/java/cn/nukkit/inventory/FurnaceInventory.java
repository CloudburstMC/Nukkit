package cn.nukkit.inventory;

import cn.nukkit.blockentity.Furnace;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceInventory extends ContainerInventory {

    public static final int SLOT_SMELTING = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_RESULT = 2;

    public FurnaceInventory(Furnace furnace, InventoryType inventoryType) {
        super(furnace, inventoryType);
    }

    @Override
    public Furnace getHolder() {
        return (Furnace) this.holder;
    }

    public Item getResult() {
        return this.getItem(SLOT_RESULT);
    }

    public Item getFuel() {
        return this.getItem(SLOT_FUEL);
    }

    public Item getSmelting() {
        return this.getItem(SLOT_SMELTING);
    }

    public boolean setResult(Item item) {
        return this.setItem(SLOT_RESULT, item);
    }

    public boolean setFuel(Item item) {
        return this.setItem(SLOT_FUEL, item);
    }

    public boolean setSmelting(Item item) {
        return this.setItem(SLOT_SMELTING, item);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().scheduleUpdate();
    }
}
