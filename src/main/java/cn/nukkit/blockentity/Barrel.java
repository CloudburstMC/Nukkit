package cn.nukkit.blockentity;

import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.inventory.InventoryHolder;

public interface Barrel extends BlockEntity, InventoryHolder {

    @Override
    BarrelInventory getInventory();
}
