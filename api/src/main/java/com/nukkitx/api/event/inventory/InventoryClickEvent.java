package com.nukkitx.api.event.inventory;

import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.inventory.Inventory;
import com.nukkitx.api.item.ItemStack;

public class InventoryClickEvent implements InventoryEvent, Cancellable {
    private final Inventory inventory;
    private final int slot;
    private final ItemStack sourceItem;
    private final ItemStack heldItem;
    private final ItemStack leftItem;
    private boolean cancelled;

    public InventoryClickEvent(Inventory inventory, int slot, ItemStack sourceItem, ItemStack heldItem, ItemStack leftItem) {
        this.inventory = inventory;
        this.slot = slot;
        this.sourceItem = sourceItem;
        this.heldItem = heldItem;
        this.leftItem = leftItem;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getSourceItem() {
        return sourceItem;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public ItemStack getLeftItem() {
        return leftItem;
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