package cn.nukkit.blockentity;

import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.ShulkerBoxInventory;

public interface ShulkerBox extends BlockEntity, InventoryHolder {

    @Override
    ShulkerBoxInventory getInventory();
}
