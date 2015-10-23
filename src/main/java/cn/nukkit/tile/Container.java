package cn.nukkit.tile;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Container {

    Item getItem(int index);

    void setItem(int index, Item item);

    int getSize();
}
