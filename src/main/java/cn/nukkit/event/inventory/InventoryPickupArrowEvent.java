package cn.nukkit.event.inventory;

import cn.nukkit.entity.impl.projectile.EntityArrow;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryPickupArrowEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final EntityArrow arrow;

    public InventoryPickupArrowEvent(Inventory inventory, EntityArrow arrow) {
        super(inventory);
        this.arrow = arrow;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityArrow getArrow() {
        return arrow;
    }
}