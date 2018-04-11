/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.inventory;

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.inventory.InventoryType;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.item.NukkitItemInstance;
import cn.nukkit.server.network.minecraft.session.PlayerSession;
import com.google.common.base.Preconditions;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class NukkitInventory implements Inventory {
    private final ItemInstance[] contents;
    @Getter
    private final List<InventoryObserver> observers = new CopyOnWriteArrayList<>();
    protected final NukkitInventoryType type;

    public NukkitInventory(NukkitInventoryType type) {
        this.contents = new ItemInstance[type.getSize()];
        this.type = type;
    }

    @Override
    public Optional<ItemInstance> getItem(int slot) {
        checkSlot(slot);
        return Optional.ofNullable(contents[slot]);
    }

    @Override
    public void setItem(int slot, @Nullable ItemInstance item) {
        setItem(slot, item, null);
    }

    public void setItem(int slot, @Nullable ItemInstance item, PlayerSession session) {
        checkSlot(slot);

        if (isItemNull(item)) {
            clearItem(slot);
        } else {
            checkItem(item);
            ItemInstance oldItem = contents[slot];
            contents[slot] = item;
            for (InventoryObserver observer : observers) {
                observer.onInventorySlotChange(slot, oldItem, item, this, session);
            }
        }
    }

    @Override
    public boolean addItem(@Nonnull ItemInstance item) {
        return addItem(item, null);
    }


    public boolean addItem(ItemInstance item, PlayerSession session) {
        if (isItemNull(item)) {
            return false;
        }
        checkItem(item);

        TIntObjectMap<ItemInstance> mergable = findMergable(item);
        if (mergable.isEmpty()) {
            return addDirectly(item, session);
        } else {
            int amount = item.getAmount();
            final int maxStackSize = item.getItemType().getMaximumStackSize();

            TIntObjectIterator<ItemInstance> iterator = mergable.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                int addableAmount = maxStackSize - iterator.value().getAmount();
                if (addableAmount <= 0) {
                    // No room. Might as well remove.
                    iterator.remove();
                    continue;
                }

                amount -= addableAmount;
            }

            int requiredSlots = 0;
            if (amount > 0) {
                // Need to use extra slots
                requiredSlots = (int) Math.ceil((float) amount / maxStackSize);
                if (requiredSlots > getEmptySlots()) {
                    return false;
                }
            }

            int itemAmount = item.getAmount();
            TIntObjectIterator<ItemInstance> itemIter = mergable.iterator();
            // Add to existing items
            while (itemIter.hasNext()) {
                if (itemAmount <= 0) {
                    return true;
                }
                itemIter.advance();

                int addableAmount = maxStackSize - itemIter.value().getAmount();

                int amountToAdd = Math.max(itemAmount, addableAmount);
                setItem(itemIter.key(), itemIter.value().toBuilder().amount(itemIter.value().getAmount() + amountToAdd).build(),
                        session);
                itemAmount -= amountToAdd;
            }

            for (int i = 0; i < requiredSlots; i++) {
                if (itemAmount <= 0) {
                    return true;
                }

                int toAdd = itemAmount >= maxStackSize ? maxStackSize : itemAmount;

                addDirectly(item.toBuilder().amount(toAdd).build(), session);
                itemAmount -= toAdd;
            }
            return true;
        }
    }

    private boolean addDirectly(@Nonnull ItemInstance item, PlayerSession session) {
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                contents[i] = item;
                for (InventoryObserver observer : observers) {
                    observer.onInventorySlotChange(i, null, item, this, session);
                }
                return true;
            }
        }
        return false;
    }

    private TIntObjectMap<ItemInstance> findMergable(@Nonnull ItemInstance item) {
        TIntObjectMap<ItemInstance> mergable = new TIntObjectHashMap<>();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                if (contents[i].isMergeable(item)) {
                    mergable.put(i, contents[i]);
                }
            }
        }
        return mergable;
    }

    @Override
    public void clearItem(int slot) {
        clearItem(slot, null);
    }

    @Override
    public int getEmptySlots() {
        int free = 0;
        for (ItemInstance content : contents) {
            if (content == null) {
                free++;
            }
        }
        return free;
    }

    private void clearItem(int slot, PlayerSession session) {
        checkSlot(slot);
        ItemInstance item = contents[slot];
        if (item != null) {
            contents[slot] = null;
            for (InventoryObserver observer : observers) {
                observer.onInventorySlotChange(slot, item, null, this, session);
            }
        }
    }

    @Override
    public void clearAll() {
        Arrays.fill(contents, null);
        for (InventoryObserver observer : observers) {
            observer.onInventoryContentsChange(contents, this);
        }
    }

    @Override
    public ItemInstance[] getAllContents() {
        return Arrays.copyOf(contents, contents.length);
    }

    @Override
    public void setAllContents(@Nonnull ItemInstance[] contents) {
        Preconditions.checkNotNull(contents, "contents");
        Preconditions.checkArgument(contents.length == this.contents.length,
                "contents' length was %s. Inventory's size is %s", contents.length, this.contents.length);
        for (ItemInstance content : contents) {
            checkItem(content);
        }
        System.arraycopy(contents, 0, this.contents, 0, this.contents.length);
        for (int i = 0; i < contents.length; i++) {
            if (isItemNull(contents[i])) {
                contents[i] = null;
            }
        }
        for (InventoryObserver observer : observers) {
            observer.onInventoryContentsChange(contents, this);
        }
    }

    @Override
    public int getInventorySize() {
        return contents.length;
    }

    @Override
    public InventoryType getInventoryType() {
        return type.toApi();
    }

    private void checkSlot(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < contents.length);
    }

    private static boolean isItemNull(@Nullable ItemInstance item) {
        return item == null || item.getItemType() == BlockTypes.AIR || item.getAmount() == 0;
    }

    private static void checkItem(@Nonnull ItemInstance item) {
        Preconditions.checkArgument(item instanceof NukkitItemInstance, "Invalid item found");
    }

    public Collection<InventoryObserver> getObservers() {
        return observers;
    }
}
