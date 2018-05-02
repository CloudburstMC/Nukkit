package com.nukkitx.api.event.inventory;

import com.nukkitx.api.entity.misc.DroppedItem;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.inventory.Inventory;

public class InventoryPickupItemEvent implements InventoryEvent, Cancellable {
    private final Inventory inventory;
    private final DroppedItem item;
    private boolean cancelled;

    public InventoryPickupItemEvent(Inventory inventory, DroppedItem item) {
        this.inventory = inventory;
        this.item = item;
    }

    public DroppedItem getItem() {
        return item;
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