package cn.nukkit.blockentity;

import cn.nukkit.inventory.BrewingInventory;

public interface BrewingStand extends BlockEntity, ContainerBlockEntity {

    @Override
    BrewingInventory getInventory();

    short getCookTime();

    void setCookTime(int cookTime);

    short getFuelAmount();

    void setFuelAmount(int fuelAmount);
}
