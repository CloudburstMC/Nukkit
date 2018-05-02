package com.nukkitx.api.event.inventory;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.inventory.Inventory;

public class InventoryOpenEvent implements InventoryEvent, Cancellable {
    private final Inventory inventory;
    private final Player who;
    private boolean cancelled;

    public InventoryOpenEvent(Inventory inventory, Player who) {
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}