package com.nukkitx.server.inventory;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.inventory.BrewingInventory;
import com.nukkitx.api.item.ItemInstance;

import javax.annotation.Nullable;
import java.util.Optional;

public class NukkitBrewingInventory extends NukkitInventory implements BrewingInventory {
    private final Vector3i position;

    public NukkitBrewingInventory(Vector3i position) {
        super(NukkitInventoryType.BREWING_STAND);
        this.position = position;
    }

    @Override
    public Vector3i getPosition() {
        return position;
    }

    @Override
    public Optional<ItemInstance> getIngredient() {
        return getItem(0);
    }

    @Override
    public void setIngredient(@Nullable ItemInstance item) {
        setItem(0, item);
    }

    @Override
    public Optional<ItemInstance> getFuel() {
        return getItem(4);
    }

    @Override
    public void setFuel(@Nullable ItemInstance item) {
        setItem(4, item);
    }
}
