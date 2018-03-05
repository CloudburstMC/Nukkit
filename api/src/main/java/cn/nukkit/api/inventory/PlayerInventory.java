package cn.nukkit.api.inventory;

import cn.nukkit.api.Player;
import cn.nukkit.api.item.ItemInstance;

import java.util.Optional;

public interface PlayerInventory extends Inventory {

    Player getPlayer();

    int[] getHotbarLinks();

    int getHeldHotbarSlot();

    void setHeldHotbarSlot(int slot);

    void setHeldHotbarSlot(int slot, boolean sendToPlayer);

    Optional<ItemInstance> getItemInHand();
}
