package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.item.ItemInstance;

public interface ItemFrameBlockEntity extends BlockEntity {

    ItemInstance getItem();

    void setItem(ItemInstance item);
}
