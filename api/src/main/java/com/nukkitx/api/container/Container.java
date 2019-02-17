package com.nukkitx.api.container;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Optional;
import java.util.OptionalInt;

@ParametersAreNullableByDefault
public interface Container {

    /**
     * Get container name if it exists.
     *
     * @return optional name
     */
    Optional<String> getCustomName();

    /**
     * Give the container a custom name or set as null for default.
     *
     * @param name custom name
     */
    void setCustomName(String name);

    /**
     * Whether the container has a custom name.
     *
     * @return custom name
     */
    boolean hasCustomName();

    /**
     * Get item in given slot if it exists
     *
     * @param slot the slot to get
     * @return item or empty
     */
    ItemStack getSlot(int slot);

    /**
     * Add an item to the first empty slot or merge with other items.
     *
     * @param item item to add
     * @return successful
     */
    boolean addItem(ItemStack item);

    /**
     * Adds an item to the first empty slot
     *
     * @param item item to add
     * @return successfull
     */
    boolean addItemToFirstEmptySlot(ItemStack item);

    /**
     * Set an item at a given slot.
     *
     * @param slot slot to set
     * @param item item to set
     */
    void setSlot(int slot, ItemStack item);

    /**
     * Clear specific slot in the {@link Container}.
     *
     * @param slot slot to clearSlot
     */
    void clearSlot(int slot);

    /**
     * Clear all slots in the {@link Container}.
     */
    void clearSlots();

    void decreaseSlot(int slot, int amount);

    /**
     * Retrieve a copy of the entire {@link Container}'s contents.
     * Changes to this array will not affect the {@link Container}
     *
     * @return contents of the {@link Container}
     */
    @Nonnull
    ItemStack[] getContents();

    /**
     * The amount of empty slots.
     *
     * @return empty slots
     */
    int getEmptySlotsCount();

    /**
     * Get the first empty slot
     *
     * @return slot index
     */
    OptionalInt getFirstEmptySlot();

    /**
     * Check to see if there is enough room for the given item, whether that be empty slots or an item that
     * {@link ItemStack#isMergeable(ItemStack)}.
     *
     * @param item item to test against
     * @return true if there space
     */
    boolean hasRoomForItem(ItemStack item);

    /**
     * Drop the contents of the {@link Container} to the world as {@link com.nukkitx.api.entity.misc.DroppedItem}s.
     *
     * @param level     level to drop the contents in
     * @param position  blockPosition at which to drop the contents
     * @param randomize random offset and motion to the items
     */
    void dropContents(@Nonnull Level level, @Nonnull Vector3f position, boolean randomize);

    /**
     * Get the size of the container
     *
     * @return container size
     */
    int getSize();
}
