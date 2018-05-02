package com.nukkitx.api.event.inventory;

import com.nukkitx.api.Player;
import com.nukkitx.api.inventory.Inventory;

public class InventoryCloseEvent implements InventoryEvent {
    private final Inventory inventory;
    private final Player who;

    public InventoryCloseEvent(Inventory inventory, Player who) {
        this.inventory = inventory;
        this.who = who;
    }

    public Player getPlayer() {
        return this.who;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
