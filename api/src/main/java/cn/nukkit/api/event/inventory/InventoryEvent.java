package cn.nukkit.api.event.inventory;

import cn.nukkit.api.Player;
import cn.nukkit.server.inventory.Inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class InventoryEvent extends Event {

    protected final Inventory inventory;

    public InventoryEvent(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player[] getViewers() {
        return this.inventory.getViewers().stream().toArray(Player[]::new);
    }

}
