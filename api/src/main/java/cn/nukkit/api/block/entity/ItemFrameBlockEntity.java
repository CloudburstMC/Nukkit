package cn.nukkit.api.block.entity;

import cn.nukkit.api.item.ItemInstance;

public interface ItemFrameBlockEntity extends BlockEntity {

    ItemInstance getItem();

    void setItem(ItemInstance item);
}
