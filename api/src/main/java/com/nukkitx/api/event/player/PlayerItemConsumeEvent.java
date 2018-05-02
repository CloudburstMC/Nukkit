package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemInstance;

/**
 * Called when a player eats something
 */
public class PlayerItemConsumeEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private boolean cancelled;

    public PlayerItemConsumeEvent(Player player, ItemInstance item) {
        this.player = player;
        this.item = item;
    }

    public ItemInstance getItem() {
        return item;
    }

    @Override
    public Player getPlayer() {
        return player;
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
