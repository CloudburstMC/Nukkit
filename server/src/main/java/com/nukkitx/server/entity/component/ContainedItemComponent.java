package com.nukkitx.server.entity.component;

import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.entity.component.ContainedItem;
import com.nukkitx.api.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainedItemComponent implements ContainedItem {
    private ItemStack item;
    private volatile boolean stale = false;

    public ContainedItemComponent(@Nonnull ItemStack item) {
        Preconditions.checkNotNull(item, "item");
        Preconditions.checkArgument(item.getItemType() != BlockTypes.AIR);
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void setItem(@Nonnull ItemStack item) {
        Preconditions.checkNotNull(item);
        if (!this.item.equals(item)) {
            this.item = item;
            stale = true;
        }
    }

    public boolean isStale() {
        return stale;
    }

    public void refresh() {
        stale = false;
    }
}
