package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemStack;

public class PlayerGlassBottleFillEvent implements PlayerEvent, Cancellable {
    protected final ItemStack item;
    private final Player player;
    protected final Block target;
    private boolean cancelled;

    public PlayerGlassBottleFillEvent(Player player, Block target, ItemStack item) {
        this.player = player;
        this.target = target;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public Block getBlock() {
        return target;
    }

    public Block getTarget() {
        return target;
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
