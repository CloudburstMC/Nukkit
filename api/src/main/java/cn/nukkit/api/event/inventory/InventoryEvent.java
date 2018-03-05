package cn.nukkit.api.event.inventory;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Event;
import cn.nukkit.api.inventory.Inventory;

import java.util.Collection;

public interface InventoryEvent extends Event {

    Inventory getInventory();

    default Collection<Player> getObservers() {
        return getInventory().getObservers();
    }
}
