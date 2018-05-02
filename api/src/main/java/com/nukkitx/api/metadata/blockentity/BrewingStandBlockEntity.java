package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.inventory.BrewingInventory;
import com.nukkitx.api.inventory.InventoryHolder;

import javax.annotation.Nullable;
import java.util.Optional;

public interface BrewingStandBlockEntity extends BlockEntity, InventoryHolder {

    Optional<String> getName();

    void setName(@Nullable String name);

    int getFuelAmount();

    void setFuelAmount(int fuelAmount);

    int getFuelTotal();

    void setFuelTotal(int fuelTotal);

    int getBrewTime();

    void setBrewTime(int brewTime);

    boolean canBrew();

    void brew();

    boolean isFinished();

    @Override
    BrewingInventory getInventory();
}
