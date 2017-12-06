package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;

/**
 * Created by CreeperFace on 1. 1. 2017.
 */
public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {


    private static final HandlerList handlers = new HandlerList();

    protected final Entity entity;
    protected final Item item;

    public PlayerInteractEntityEvent(Player player, Entity entity, Item item) {
        this.player = player;
        this.entity = entity;
        this.item = item;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Item getItem() {
        return this.item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
