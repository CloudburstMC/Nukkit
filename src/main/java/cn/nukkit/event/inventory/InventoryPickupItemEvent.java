package cn.nukkit.event.inventory;

import cn.nukkit.entity.DroppedItem;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryPickupItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private DroppedItem item;

    public InventoryPickupItemEvent(Inventory inventory, DroppedItem item) {
        super(inventory);
        this.item = item;
    }

    public DroppedItem getItem() {
        return item;
    }
}