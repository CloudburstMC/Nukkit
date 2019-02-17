package com.nukkitx.api.crafting.recipe;

import com.nukkitx.api.item.ItemStack;

import java.util.UUID;

public interface Recipe {

    UUID getId();

    ItemStack[] getItems();

    boolean isMultiRecipe();

    boolean isAnyMetadataValue(ItemStack item);

    int getHeight();

    int getWidth();
}
