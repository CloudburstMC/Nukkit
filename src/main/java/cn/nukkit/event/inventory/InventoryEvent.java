package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.inventory.Inventory;

/**
 * @author MagicDroidX (Nukkit Project)
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
        return this.inventory.getViewers().toArray(new Player[0]);
    }

}
