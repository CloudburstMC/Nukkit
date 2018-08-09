package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.inventory.Inventory;
import com.nukkitx.api.inventory.InventoryHolder;

public interface ChestBlockEntity extends NameableBlockEntity, InventoryHolder {

    boolean isTrapped();

    @Override
    Inventory getInventory();

    boolean isLargeChest();
}
