package cn.nukkit.tile;

import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.InventoryHolder;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Chest extends Spawnable implements InventoryHolder, Container {

    protected ChestInventory inventory;


    public ChestInventory getRealInventory() {
        return inventory;
    }
}
