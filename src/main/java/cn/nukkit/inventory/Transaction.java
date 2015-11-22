package cn.nukkit.inventory;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Transaction {

    Inventory getInventory();

    int getSlot();

    Item getSourceItem();

    Item getTargetItem();

    long getCreationTime();
}
