package com.nukkitx.api.inventory;

import com.nukkitx.api.Player;
import com.nukkitx.api.item.ItemInstance;

import java.util.Optional;

public interface PlayerInventory extends Inventory {

    Player getPlayer();

    int[] getHotbarLinks();

    int getHeldHotbarSlot();

    void setHeldHotbarSlot(int slot);

    void setHeldHotbarSlot(int slot, boolean sendToPlayer);

    Optional<ItemInstance> getItemInHand();
}
