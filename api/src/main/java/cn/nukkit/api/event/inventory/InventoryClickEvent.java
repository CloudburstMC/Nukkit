package cn.nukkit.api.event.inventory;

import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.item.ItemInstance;

public class InventoryClickEvent implements InventoryEvent, Cancellable {
    private final Inventory inventory;
    private final int slot;
    private final ItemInstance sourceItem;
    private final ItemInstance heldItem;
    private final ItemInstance leftItem;
    private boolean cancelled;

    public InventoryClickEvent(Inventory inventory, int slot, ItemInstance sourceItem, ItemInstance heldItem, ItemInstance leftItem) {
        this.inventory = inventory;
        this.slot = slot;
        this.sourceItem = sourceItem;
        this.heldItem = heldItem;
        this.leftItem = leftItem;
    }

    public int getSlot() {
        return slot;
    }

    public ItemInstance getSourceItem() {
        return sourceItem;
    }

    public ItemInstance getHeldItem() {
        return heldItem;
    }

    public ItemInstance getLeftItem() {
        return leftItem;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}