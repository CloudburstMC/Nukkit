package com.nukkitx.api.entity.component;

import com.nukkitx.api.item.ItemInstance;

import javax.annotation.Nonnull;

public interface ContainedItem extends EntityComponent {

    ItemInstance getItem();

    void setItem(@Nonnull ItemInstance item);
}
