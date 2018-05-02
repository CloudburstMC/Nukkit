package com.nukkitx.api.event.block;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemInstance;

public class BlockPlaceEvent implements BlockEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private final Block oldBlock;
    private final Block against;
    private final BlockState newBlockState;
    private boolean cancelled;

    public BlockPlaceEvent(Player player, Block against, Block oldBlock, BlockState newBlockState, ItemInstance item) {
        this.player = player;
        this.against = against;
        this.oldBlock = oldBlock;
        this.newBlockState = newBlockState;
        this.item = item;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemInstance getItem() {
        return item;
    }

    public BlockState getNewBlockState() {
        return newBlockState;
    }

    public Block getAgainst() {
        return against;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Block getBlock() {
        return oldBlock;
    }
}
