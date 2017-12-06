package cn.nukkit.api.event.inventory;

import cn.nukkit.server.entity.item.EntityItem;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.inventory.Inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryPickupItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final EntityItem item;

    public InventoryPickupItemEvent(Inventory inventory, EntityItem item) {
        super(inventory);
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityItem getItem() {
        return item;
    }
}