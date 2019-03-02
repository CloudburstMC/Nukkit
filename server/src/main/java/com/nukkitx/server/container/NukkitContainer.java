package com.nukkitx.server.container;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.nukkitx.api.container.Container;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.level.Level;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@ParametersAreNullableByDefault
public class NukkitContainer implements Container {
    private final List<ContainerContentListener> contentListeners = new CopyOnWriteArrayList<>();
    private final List<ContainerSizeListener> sizeListeners = new CopyOnWriteArrayList<>();
    private final ContainerType type;
    ItemStack[] contents = new ItemStack[27];
    private volatile String name = null;

    public NukkitContainer(@Nonnull ContainerType type, String name) {
        this(type);
        this.name = name;
    }

    public NukkitContainer(ContainerType type) {
        this.type = type;
    }

    @Override
    public boolean hasCustomName() {
        return name != null;
    }

    @Nonnull
    @Override
    public Optional<String> getCustomName() {
        return Optional.ofNullable(name);
    }

    @Override
    public void setCustomName(@Nullable String name) {
        this.name = name;
    }

    public synchronized ItemStack getSlot(int slot) {
        checkSlot(slot);
        return ItemUtils.getItemOrEmpty(contents[slot]);
    }

    public synchronized int getSize() {
        return contents.length;
    }

    public ContainerType getContainerType() {
        return type;
    }

    @Override
    public boolean addItem(ItemStack item) {
        return addItem(item, null);
    }

    public boolean addItem(ItemStack item, @Nullable NukkitPlayerSession session) {

        ItemUtils.checkValidItem(item);

        final TIntObjectMap<ItemStack> mergeableItems = findMergable(item);
        final int maxStackSize = item.getItemType().getMaximumStackSize();
        int amountLeft = item.getAmount();

        if (!mergeableItems.isEmpty()) {
            TIntObjectIterator<ItemStack> iterator = mergeableItems.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                int addableAmount = maxStackSize - iterator.value().getAmount();
                if (addableAmount <= 0) {
                    // No room. Might as well remove.
                    iterator.remove();
                    continue;
                }

                amountLeft -= addableAmount;
            }
        }

        int requiredSlots = 0;
        if (amountLeft > 0) {
            // Need to use extra slots
            requiredSlots = (int) Math.ceil((float) amountLeft / maxStackSize);
            if (requiredSlots > getEmptySlotsCount()) {
                return false;
            }
        }

        amountLeft = item.getAmount();
        if (!mergeableItems.isEmpty()) {
            TIntObjectIterator<ItemStack> itemIter = mergeableItems.iterator();
            // Add to existing items
            while (itemIter.hasNext()) {
                if (amountLeft <= 0) {
                    return true;
                }
                itemIter.advance();

                int addableAmount = maxStackSize - itemIter.value().getAmount();

                int amountToAdd = Math.min(amountLeft, addableAmount);
                setItem(itemIter.key(), itemIter.value().toBuilder().amount(itemIter.value().getAmount() + amountToAdd).build(),
                        session);
                amountLeft -= amountToAdd;
            }
        }

        for (int i = 0; i < requiredSlots; i++) {
            if (amountLeft <= 0) {
                return true;
            }

            int toAdd = amountLeft >= maxStackSize ? maxStackSize : amountLeft;

            if (!addItemToFirstEmptySlot(item.toBuilder().amount(toAdd).build(), session)) {
                throw new IllegalStateException("Not enough slots since checked.");
            }
            amountLeft -= toAdd;
        }
        return true;
    }

    @Override
    public boolean addItemToFirstEmptySlot(ItemStack item) {
        return addItemToFirstEmptySlot(item, null);
    }

    public boolean addItemToFirstEmptySlot(ItemStack item, @Nullable NukkitPlayerSession session) {
        OptionalInt slot = getFirstEmptySlot();
        if (!slot.isPresent() || ItemStack.isInvalid(item)) {
            return false;
        }

        setItem(slot.getAsInt(), item, session);
        return true;
    }

    @Override
    public void setSlot(int slot, @Nullable ItemStack item) {
        setItem(slot, item, null);
    }

    public synchronized void setItem(int slot, @Nullable ItemStack item, @Nullable NukkitPlayerSession session) {
        checkSlot(slot);

        if (ItemStack.isInvalid(item)) {
            clearItem(slot, session);
        } else {
            ItemUtils.checkValidItem(item);
            ItemStack oldItem = contents[slot];
            contents[slot] = item;
            for (ContainerContentListener listener : contentListeners) {
                listener.onSlotChange(slot, oldItem, item, this, session);
            }
        }
    }

    @Override
    public synchronized void decreaseSlot(int slot, int amount) {
        checkSlot(slot);

        ItemStack item = contents[slot];
        if (item != null) {
            if (item.getAmount() > amount) {
                contents[slot] = item.toBuilder()
                        .amount(item.getAmount() - amount)
                        .build();
            } else {
                contents[slot] = null;
            }
        }
    }

    @Override
    public int getEmptySlotsCount() {
        return getEmptySlotsCount(0, contents.length);
    }

    protected synchronized int getEmptySlotsCount(int offset, int length) {
        checkSlot(offset);
        final int adjustedLength = offset + length;
        checkSlot(adjustedLength - 1);

        int emptySlots = 0;
        for (int i = offset; i < adjustedLength; i++) {
            if (contents[i] == null) {
                emptySlots++;
            }
        }
        return emptySlots;
    }

    @Override
    public synchronized OptionalInt getFirstEmptySlot() {
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public void clearSlot(int slot) {
        clearItem(slot, null);
    }

    public synchronized void clearItem(int slot, @Nullable NukkitPlayerSession session) {
        checkSlot(slot);
        ItemStack item = contents[slot];
        if (item != null) {
            contents[slot] = null;
            for (ContainerContentListener listener : contentListeners) {
                listener.onSlotChange(slot, item, null, this, session);
            }
        }
    }

    @Override
    public synchronized void clearSlots() {
        Arrays.fill(contents, null);
        for (ContainerContentListener listener : contentListeners) {
            listener.onContainerContentChange(getContents(), this);
        }
    }

    @Nonnull
    @Override
    public synchronized ItemStack[] getContents() {
        return Arrays.copyOf(contents, contents.length);
    }

    @Override
    public boolean hasRoomForItem(ItemStack item) {
        if (ItemStack.isInvalid(item)) {
            return false;
        }

        // If there are enough empty slots, we don't need to check mergeability.
        if (getEmptySlotsCount() > 0) {
            return true;
        }

        Collection<ItemStack> mergableItems = findMergable(item).valueCollection();

        final int maxStackSize = item.getItemType().getMaximumStackSize();
        int amount = item.getAmount();

        for (ItemStack mergableItem : mergableItems) {
            amount -= (maxStackSize - mergableItem.getAmount());
        }

        return amount <= 0;
    }

    @Override
    public void dropContents(@Nonnull Level level, @Nonnull Vector3f position, boolean randomize) {
        // TODO: 18/12/2018 drop content method
    }

    public void open(@Nonnull NukkitPlayerSession session) {
    }

    public void close(@Nonnull NukkitPlayerSession session) {
    }

    protected synchronized void setContainerSize(int size) {
        Preconditions.checkArgument(size > 0, "Size must be bigger than zero");

        if (getSize() == size) {
            return;
        }

        contents = Arrays.copyOf(contents, size);

        for (ContainerSizeListener listener : sizeListeners) {
            listener.onContainerSizeChange(this, size);
        }
    }

    public void addSizeListener(ContainerSizeListener listener) {
        Preconditions.checkNotNull(listener, "listener");
        sizeListeners.add(listener);
    }

    public void removeSizeListener(ContainerSizeListener listener) {
        Preconditions.checkNotNull(listener, "listener");
        sizeListeners.remove(listener);
    }

    public void addContentListener(ContainerContentListener listener) {
        Preconditions.checkNotNull(listener, "listener");
        contentListeners.add(listener);
    }

    public void removeContentListener(ContainerContentListener listener) {
        Preconditions.checkNotNull(listener, "listener");
        contentListeners.remove(listener);
    }

    public int getMaxStackSize() {
        return 254;
    }

    public void onOpen(NukkitPlayerSession session) {
    }

    public void onClose(NukkitPlayerSession session) {
    }

    private synchronized TIntObjectMap<ItemStack> findMergable(@Nonnull ItemStack item) {
        TIntObjectMap<ItemStack> mergable = new TIntObjectHashMap<>();
        for (int i = 0; i < getSize(); i++) {
            ItemStack content = contents[i];
            if (content != null && content.isMergeable(item)) {
                mergable.put(i, content);
            }
        }
        return mergable;
    }

    void checkSlot(int slot) {
        Preconditions.checkElementIndex(slot, contents.length, "slot");
    }
}
