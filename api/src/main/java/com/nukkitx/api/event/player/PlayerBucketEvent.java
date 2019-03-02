package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.util.data.BlockFace;

abstract class PlayerBucketEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Block blockClicked;
    private final BlockFace blockFace;
    private final ItemStack bucket;
    private boolean cancelled;
    private ItemStack item;


    public PlayerBucketEvent(Player who, Block blockClicked, BlockFace blockFace, ItemStack bucket, ItemStack itemInHand) {
        this.player = who;
        this.blockClicked = blockClicked;
        this.blockFace = blockFace;
        this.item = itemInHand;
        this.bucket = bucket;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Block getBlockClicked() {
        return blockClicked;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public ItemStack getBucket() {
        return bucket;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
