package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Inventory {

    int MAX_STACK = 64;


    int getSize();

    int getMaxStackSize();

    void setMaxStackSize(int size);

    String getName();

    String getTitle();

    Item getItem(int index);

    default boolean setItem(int index, Item item) {
        return setItem(index, item, true);
    }

    boolean setItem(int index, Item item, boolean send);

    Item[] addItem(Item... slots);

    boolean canAddItem(Item item);

    Item[] removeItem(Item... slots);

    Map<Integer, Item> getContents();

    void setContents(Map<Integer, Item> items);

    void sendContents(Player player);

    void sendContents(Player... players);

    void sendContents(Collection<Player> players);

    void sendSlot(int index, Player player);

    void sendSlot(int index, Player... players);

    void sendSlot(int index, Collection<Player> players);

    boolean contains(Item item);

    Map<Integer, Item> all(Item item);

    default int first(Item item) {
        return first(item, false);
    }

    /**
     * Search for the first occurrence of target item
     *
     * @param item  target item
     * @param exact if true the item count will be must match
     * @return the first index containing the item
     */
    int first(Item item, boolean exact);

    default int firstFit(Item item) {
        return firstFit(item, false);
    }

    /**
     * Returns the first slot where item fits to
     *
     * @param item   item to search for
     * @param single if false the item count will be used. Otherwise it'll be 1
     * @return the first slot index that item fits to
     */
    int firstFit(Item item, boolean single);

    /**
     * Search for the first empty slot
     *
     * @return the first slot index
     */
    int firstEmpty();

    /**
     * Search for the first slot containing an item
     *
     * @return the first non-empty slot
     */
    int firstNonEmpty();

    /**
     * Returns how much space remains for the target item
     *
     * @param item target item
     * @return amount if free space
     */
    int getFreeSpace(Item item);

    /**
     * Decrease item count in the given slot
     *
     * @param slot target slot index
     */
    void decreaseCount(int slot);

    /**
     * Increase item count in the given slot
     *
     * @param slot target slot index
     */
    void increaseCount(int slot);

    void remove(Item item);

    default boolean clear(int index) {
        return clear(index, true);
    }

    boolean clear(int index, boolean send);

    void clearAll();

    boolean isFull();

    boolean isEmpty();

    Set<Player> getViewers();

    InventoryType getType();

    InventoryHolder getHolder();

    void onOpen(Player who);

    boolean open(Player who);

    void close(Player who);

    void onClose(Player who);

    void onSlotChange(int index, Item before, boolean send);
}
