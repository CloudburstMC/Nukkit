package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemInstance;

public class PlayerItemHeldEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private final int slot;
    private boolean cancelled;

    public PlayerItemHeldEvent(Player player, ItemInstance item, int slot) {
        this.player = player;
        this.item = item;
        this.slot = slot;
    }

    public ItemInstance getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
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
