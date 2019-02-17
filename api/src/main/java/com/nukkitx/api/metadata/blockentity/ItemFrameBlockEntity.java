package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.item.ItemStack;

public interface ItemFrameBlockEntity extends BlockEntity {

    ItemStack getItem();

    void setItem(ItemStack item);
}
