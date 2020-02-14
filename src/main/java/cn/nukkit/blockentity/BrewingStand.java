package cn.nukkit.blockentity;

import cn.nukkit.inventory.BrewingInventory;
import cn.nukkit.inventory.InventoryHolder;

public interface BrewingStand extends BlockEntity, InventoryHolder {

    @Override
    BrewingInventory getInventory();

    short getCookTime();

    void setCookTime(int cookTime);

    short getFuelAmount();

    void setFuelAmount(int fuelAmount);
}
