package com.nukkitx.server.inventory;

import com.nukkitx.api.item.ItemStack;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.data.ContainerId;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

import javax.annotation.Nullable;

public class NukkitPlayerInventory extends NukkitInventory {
    private int selectedSlot = 0;
    private int selectedId = ContainerId.INVENTORY;

    public NukkitPlayerInventory(PlayerSession session) {
        super(session);
    }

    public void setItem(int slot, ItemStack item, PlayerSession session, int id) {
        if (id == ContainerId.INVENTORY) {
            setItem(slot, item, session);
        }
    }

    public ItemStack getItem(int slot, int id) {
        if (id == ContainerId.INVENTORY) {
            return getSlot(slot);
        }
        return ItemUtils.AIR;
    }

    public void clearSlot(int slot, int id) {
        if (id == ContainerId.INVENTORY) {
            clearSlot(slot);
        }
    }

    public void setContainerSize(int size, int id) {
        if (id == ContainerId.INVENTORY) {
            setContainerSize(size);
        }
    }

    public int getSize(int id) {
        if (id != ContainerId.INVENTORY) {
            return 0;
        } else {
            return getSize();
        }
    }

    public void selectSlot(int slot, int id) {
        Preconditions.checkNotNull(id, "id");
        Preconditions.checkArgument(slot >= 0 && getHotbarSize() > slot, "slot not in range");
        this.selectedId = id;
        this.selectedSlot = slot;
    }

    public ItemStack getSelectedItem() {
        return getSlot(selectedSlot);
    }

    public void setSelectedItem(@Nullable ItemStack item, @Nullable PlayerSession session) {
        setItem(selectedSlot, item, session);
    }

    public int getSelectedId() {
        return selectedId;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }
}
