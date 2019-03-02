package com.nukkitx.api.entity.component;

import com.nukkitx.api.item.ItemStack;

import javax.annotation.Nonnull;

public interface ContainedItem extends EntityComponent {

    ItemStack getItem();

    void setItem(@Nonnull ItemStack item);
}
