package cn.nukkit.api.block.entity;

import cn.nukkit.api.inventory.BrewingInventory;
import cn.nukkit.api.inventory.InventoryHolder;

import javax.annotation.Nullable;
import java.util.Optional;

public interface BrewingStandBlockEntity extends BlockEntity, InventoryHolder {

    Optional<String> getName();

    void setName(@Nullable String name);

    @Override
    BrewingInventory getInventory();
}
