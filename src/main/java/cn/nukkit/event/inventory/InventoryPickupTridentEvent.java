package cn.nukkit.event.inventory;

import cn.nukkit.api.Since;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;

@Since("1.3.2.0-PN")
public class InventoryPickupTridentEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Since("1.3.2.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityThrownTrident trident;

    @Since("1.3.2.0-PN")
    public InventoryPickupTridentEvent(Inventory inventory, EntityThrownTrident trident) {
        super(inventory);
        this.trident = trident;
    }

    @Since("1.3.2.0-PN")
    public EntityThrownTrident getTrident() {
        return trident;
    }
}
