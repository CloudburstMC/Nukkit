package cn.nukkit.api.metadata.blockentity;

import cn.nukkit.api.inventory.BrewingInventory;
import cn.nukkit.api.inventory.InventoryHolder;

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
