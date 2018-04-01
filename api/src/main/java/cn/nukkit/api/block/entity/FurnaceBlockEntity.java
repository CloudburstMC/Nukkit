package cn.nukkit.api.block.entity;

import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.inventory.InventoryHolder;
import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nullable;

public interface FurnaceBlockEntity extends BlockEntity, InventoryHolder {

    boolean isFuel(@Nullable ItemInstance item);

    int getBurnDuration(@Nullable ItemInstance item);

    boolean isIngredient(@Nullable ItemInstance item);

    @Override
    Inventory getInventory();
}
