package cn.nukkit.api.event.inventory;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.inventory.Inventory;

public interface InventoryEvent extends Event {

    Inventory getInventory();
}
