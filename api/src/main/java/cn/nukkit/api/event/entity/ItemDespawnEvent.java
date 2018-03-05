package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.item.DroppedItem;
import cn.nukkit.api.event.Cancellable;

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
