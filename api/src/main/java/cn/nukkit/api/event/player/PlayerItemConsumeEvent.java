package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;

/**
 * Called when a player eats something
 */
public class PlayerItemConsumeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item item;

    public PlayerItemConsumeEvent(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    public Item getItem() {
        return this.item.clone();
    }
}
