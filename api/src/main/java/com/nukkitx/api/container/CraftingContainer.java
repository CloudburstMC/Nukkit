package com.nukkitx.api.container;

import com.nukkitx.api.item.ItemStack;

public interface CraftingContainer extends Container {

    /**
     * Get item based on x and y coordinate on the crafting grid
     *
     * @param x coordinate
     * @param y coordinate
     * @return item or empty
     */
    ItemStack getItem(int x, int y);
}
