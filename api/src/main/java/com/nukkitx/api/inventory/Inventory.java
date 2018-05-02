package com.nukkitx.api.inventory;

import com.nukkitx.api.item.ItemInstance;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface Inventory {

    Optional<ItemInstance> getItem(int slot);

    void setItem(int slot, @Nullable ItemInstance item);

    boolean addItem(ItemInstance item);

    void clearItem(int slot);

    int getEmptySlots();

    void clearAll();

    ItemInstance[] getAllContents();

    void setAllContents(ItemInstance[] contents);

    int getInventorySize();

    InventoryType getInventoryType();
}
