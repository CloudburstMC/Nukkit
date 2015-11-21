package cn.nukkit.event.entity;

import cn.nukkit.entity.Item;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDespawnEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ItemDespawnEvent(Item item) {
        this.entity = item;
    }

    @Override
    public Item getEntity() {
        return (Item) this.entity;
    }
}
