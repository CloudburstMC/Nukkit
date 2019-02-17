package com.nukkitx.server.crafting.recipe;

import com.nukkitx.api.item.ItemStack;

public interface CraftingRecipe extends Recipe {

    boolean matchesItems(ItemStack[][] items);

    byte getHeight();

    byte getWidth();

    default boolean needsCraftingTable() {
        return getHeight() > 2 || getWidth() > 2;
    }
}
