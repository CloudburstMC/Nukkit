package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemStack;

/**
 * Called when a player eats something
 */
public class PlayerItemConsumeEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemStack item;
    private boolean cancelled;

    public PlayerItemConsumeEvent(Player player, ItemStack item) {
        this.player = player;
        this.item = item;
    }

    public ItemStack getItem() {
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
