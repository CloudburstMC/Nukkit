package cn.nukkit.api.inventory;

import cn.nukkit.api.Player;
import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
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

    Collection<Player> getObservers();
}
