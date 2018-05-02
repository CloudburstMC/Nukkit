package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.inventory.Inventory;
import com.nukkitx.api.inventory.InventoryHolder;

public interface HopperBlockEntity extends BlockEntity, InventoryHolder {

    @Override
    Inventory getInventory();
}
