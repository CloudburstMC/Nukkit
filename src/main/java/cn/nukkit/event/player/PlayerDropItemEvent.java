package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class PlayerDropItemEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Item drop;

    public PlayerDropItemEvent(Player player, Item drop) {
        this.player = player;
        this.drop = drop;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Item getItem() {
        return this.drop;
    }
}
