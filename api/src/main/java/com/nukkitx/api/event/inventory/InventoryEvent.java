package com.nukkitx.api.event.inventory;

import com.nukkitx.api.event.Event;
import com.nukkitx.api.inventory.Inventory;

public interface InventoryEvent extends Event {

    Inventory getInventory();
}
