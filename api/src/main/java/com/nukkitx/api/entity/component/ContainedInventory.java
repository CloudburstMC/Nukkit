package com.nukkitx.api.entity.component;

import com.nukkitx.api.inventory.Inventory;

public interface ContainedInventory extends EntityComponent {

    Inventory getInventory();
}
