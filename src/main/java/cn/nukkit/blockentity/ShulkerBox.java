package cn.nukkit.blockentity;

import cn.nukkit.inventory.ShulkerBoxInventory;

public interface ShulkerBox extends BlockEntity, ContainerBlockEntity {

    @Override
    ShulkerBoxInventory getInventory();
}
