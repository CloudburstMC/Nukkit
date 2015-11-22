package cn.nukkit.event.inventory;

import cn.nukkit.entity.Arrow;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryPickupArrowEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Arrow arrow;

    public InventoryPickupArrowEvent(Inventory inventory, Arrow arrow) {
        super(inventory);
        this.arrow = arrow;
    }

    public Arrow getArrow() {
        return arrow;
    }
}