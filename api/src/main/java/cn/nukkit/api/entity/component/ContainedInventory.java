package cn.nukkit.api.entity.component;

import cn.nukkit.api.inventory.Inventory;

public interface ContainedInventory extends EntityComponent {

    Inventory getInventory();
}
