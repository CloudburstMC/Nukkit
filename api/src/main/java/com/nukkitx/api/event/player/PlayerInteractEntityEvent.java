package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemStack;

public class PlayerInteractEntityEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Entity entity;
    private final ItemStack item;
    private boolean cancelled;

    public PlayerInteractEntityEvent(Player player, Entity entity, ItemStack item) {
        this.player = player;
        this.entity = entity;
        this.item = item;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Entity getEntity() {
        return entity;
    }

    public ItemStack getItem() {
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
