package cn.nukkit.api.metadata.blockentity;

import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.inventory.InventoryHolder;

public interface HopperBlockEntity extends BlockEntity, InventoryHolder {

    @Override
    Inventory getInventory();
}
