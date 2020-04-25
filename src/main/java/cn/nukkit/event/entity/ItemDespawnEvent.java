package cn.nukkit.event.entity;

import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDespawnEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public ItemDespawnEvent(DroppedItem item) {
        this.entity = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public DroppedItem getEntity() {
        return (DroppedItem) this.entity;
    }
}
