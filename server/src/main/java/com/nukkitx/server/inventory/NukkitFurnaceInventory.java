package com.nukkitx.server.inventory;

import com.nukkitx.api.inventory.FurnaceInventory;

/**
 * @author CreeperFace
 */
public class NukkitFurnaceInventory extends NukkitInventory implements FurnaceInventory {

    public NukkitFurnaceInventory() {
        super(NukkitInventoryType.FURNACE);
    }
}
