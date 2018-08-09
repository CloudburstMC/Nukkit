package com.nukkitx.api.inventory;

import com.nukkitx.api.item.ItemInstance;

import javax.annotation.Nullable;
import java.util.Optional;

public interface BrewingInventory extends Inventory, OpenableInventory {

    Optional<ItemInstance> getIngredient();

    void setIngredient(@Nullable ItemInstance item);

    Optional<ItemInstance> getFuel();

    void setFuel(@Nullable ItemInstance item);

    Optional<ItemInstance> getPotion(int slot);

    void setPotion(int slot, @Nullable ItemInstance item);
}
