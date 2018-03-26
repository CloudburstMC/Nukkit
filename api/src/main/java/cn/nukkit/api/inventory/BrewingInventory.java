package cn.nukkit.api.inventory;

import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nullable;
import java.util.Optional;

public interface BrewingInventory extends Inventory, OpenableInventory {

    Optional<ItemInstance> getIngredient();

    void setIngredient(@Nullable ItemInstance item);

    Optional<ItemInstance> getFuel();

    void setFuel(@Nullable ItemInstance item);
}
