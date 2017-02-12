package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

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
