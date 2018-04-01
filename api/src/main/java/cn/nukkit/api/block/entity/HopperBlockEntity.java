package cn.nukkit.api.block.entity;

import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.inventory.InventoryHolder;

public interface HopperBlockEntity extends BlockEntity, InventoryHolder {

    @Override
    Inventory getInventory();
}
