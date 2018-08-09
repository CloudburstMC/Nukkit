package com.nukkitx.api.inventory;

import com.nukkitx.api.item.ItemInstance;

import java.util.Optional;

/**
 * @author CreeperFace
 */
public interface FurnaceInventory extends Inventory {

    default void setItem(SlotType slot, ItemInstance item) {
        setItem(slot.ordinal(), item);
    }

    default Optional<ItemInstance> getItem(SlotType slot) {
        return getItem(slot.ordinal());
    }

    enum SlotType {
        INGREDIENT,
        FUEL,
        RESULT
    }
}
