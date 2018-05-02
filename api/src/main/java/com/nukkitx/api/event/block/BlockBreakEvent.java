package com.nukkitx.api.event.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemInstance;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class BlockBreakEvent implements BlockEvent, Cancellable {
    private final Entity entity;
    private final Block block;
    private BlockState newState;
    private final ItemInstance withItem;
    private boolean instantBreak;
    private final List<ItemInstance> drops;
    private boolean cancelled;

    public BlockBreakEvent(Entity entity, Block block, BlockState newState, ItemInstance withItem, Collection<ItemInstance> drops, boolean instantBreak) {
        this.entity = entity;
        this.block = block;
        this.newState = newState;
        this.withItem = withItem;
        this.instantBreak = instantBreak;
        this.drops = Lists.newArrayList(drops);
    }

    public Entity getEntity() {
        return entity;
    }

    public ItemInstance getWithItem() {
        return withItem;
    }

    public boolean willInstantBreak() {
        return instantBreak;
    }

    public void setInstantBreak(boolean instantBreak) {
        this.instantBreak = instantBreak;
    }

    public Collection<ItemInstance> getDrops() {
        return drops;
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
        return block;
    }

    public BlockState getNewState() {
        return newState;
    }

    public void setNewState(@Nonnull BlockState newState) {
        Preconditions.checkNotNull(newState, "newState");
        this.newState = newState;
    }
}
