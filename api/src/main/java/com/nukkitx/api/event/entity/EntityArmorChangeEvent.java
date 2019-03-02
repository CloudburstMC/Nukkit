package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemStack;

public class EntityArmorChangeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final ItemStack oldItem;
    private final int slot;
    private ItemStack newItem;
    private boolean cancelled;

    public EntityArmorChangeEvent(Entity entity, ItemStack oldItem, ItemStack newItem, int slot) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getNewItem() {
        return newItem;
    }

    public void setNewItem(ItemStack newItem) {
        this.newItem = newItem;
    }

    public ItemStack getOldItem() {
        return oldItem;
    }

    @Override
    public Entity getEntity() {
        return entity;
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
