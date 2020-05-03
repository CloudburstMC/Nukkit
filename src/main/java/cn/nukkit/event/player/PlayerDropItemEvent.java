package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

public class PlayerDropItemEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item drop;

    public PlayerDropItemEvent(Player player, Item drop) {
        super(player);
        this.drop = drop;
    }

    public Item getItem() {
        return this.drop;
    }
}
