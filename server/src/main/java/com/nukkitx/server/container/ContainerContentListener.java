package com.nukkitx.server.container;

import com.nukkitx.api.container.Container;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An object that can view a specific inventory.
 */
@ParametersAreNonnullByDefault
public interface ContainerContentListener {

    /**
     * Called when a single slot is changed within the observed inventory.
     *
     * @param slot      slot in question
     * @param oldItem   old item in slot
     * @param newItem   new item in slot
     * @param container inventory being observed
     * @param session   session which caused the change
     */
    void onSlotChange(int slot, @Nullable ItemStack oldItem, @Nullable ItemStack newItem, Container container, @Nullable NukkitPlayerSession session);

    /**
     * Called when the entire contents of the inventory is changed within the observed inventory.
     *
     * @param contents  new contents of the inventory
     * @param container inventory being observed
     */
    void onContainerContentChange(ItemStack[] contents, Container container);
}
