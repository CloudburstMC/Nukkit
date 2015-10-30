package cn.nukkit.event.inventory;

import cn.nukkit.entity.Arrow;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.entity.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryPickupItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item item;

    public InventoryPickupItemEvent(Inventory inventory, Item item) {
        super(inventory);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}