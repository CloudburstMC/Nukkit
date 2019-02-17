package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemStack;

public class PlayerBlockPickEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Block blockClicked;
    private ItemStack item;
    private boolean cancelled;

    public PlayerBlockPickEvent(Player player, Block blockClicked, ItemStack item) {
        this.player = player;
        this.blockClicked = blockClicked;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public Block getBlockClicked() {
        return blockClicked;
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
