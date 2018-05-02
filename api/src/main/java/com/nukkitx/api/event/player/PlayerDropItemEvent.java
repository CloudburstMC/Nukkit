package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemInstance;

public class PlayerDropItemEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private boolean cancelled;

    public PlayerDropItemEvent(Player player, ItemInstance drop) {
        this.player = player;
        this.item = drop;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public ItemInstance getItem() {
        return item;
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
