package cn.nukkit.event.entity;

import cn.nukkit.entity.Item;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSpawnEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ItemSpawnEvent(Item item) {
        this.entity = item;
    }

    @Override
    public Item getEntity() {
        return (Item) this.entity;
    }
}
