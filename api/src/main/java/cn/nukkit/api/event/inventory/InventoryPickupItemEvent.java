package cn.nukkit.api.event.inventory;

import cn.nukkit.api.entity.item.DroppedItem;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.inventory.Inventory;

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