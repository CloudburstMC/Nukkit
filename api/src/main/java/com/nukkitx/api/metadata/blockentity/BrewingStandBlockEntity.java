package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.container.Container;
import com.nukkitx.api.container.ContainerHolder;

import javax.annotation.Nullable;
import java.util.Optional;

public interface BrewingStandBlockEntity extends BlockEntity, ContainerHolder {

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
    Container getContainer();
}
