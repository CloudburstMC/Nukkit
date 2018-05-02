package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemInstance;

public class EntityArmorChangeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final ItemInstance oldItem;
    private final int slot;
    private ItemInstance newItem;
    private boolean cancelled;

    public EntityArmorChangeEvent(Entity entity, ItemInstance oldItem, ItemInstance newItem, int slot) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public ItemInstance getNewItem() {
        return newItem;
    }

    public void setNewItem(ItemInstance newItem) {
        this.newItem = newItem;
    }

    public ItemInstance getOldItem() {
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
