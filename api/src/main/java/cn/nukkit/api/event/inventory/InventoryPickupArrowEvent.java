package cn.nukkit.api.event.inventory;

import cn.nukkit.server.entity.projectile.EntityArrow;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.inventory.Inventory;

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