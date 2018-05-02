package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.misc.DroppedItem;
import com.nukkitx.api.event.Cancellable;

public class ItemDespawnEvent implements EntityEvent, Cancellable {
    private final DroppedItem item;
    private boolean cancelled;

    public ItemDespawnEvent(DroppedItem item) {
        this.item = item;
    }

    @Override
    public DroppedItem getEntity() {
        return item;
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
