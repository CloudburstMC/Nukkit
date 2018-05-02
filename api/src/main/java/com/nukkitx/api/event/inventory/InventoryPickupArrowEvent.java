package com.nukkitx.api.event.inventory;

import com.nukkitx.api.entity.projectile.Arrow;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.inventory.Inventory;

public class InventoryPickupArrowEvent implements InventoryEvent, Cancellable {
    private final Inventory inventory;
    private final Arrow arrow;
    private boolean cancelled;

    public InventoryPickupArrowEvent(Inventory inventory, Arrow arrow) {
        this.inventory = inventory;
        this.arrow = arrow;
    }

    public Arrow getArrow() {
        return arrow;
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