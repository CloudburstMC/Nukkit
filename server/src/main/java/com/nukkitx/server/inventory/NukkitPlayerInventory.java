package com.nukkitx.server.inventory;

import com.google.common.base.Preconditions;
import com.nukkitx.api.inventory.PlayerInventory;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.bedrock.packet.MobEquipmentPacket;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class NukkitPlayerInventory extends NukkitInventory implements PlayerInventory {
    private final PlayerSession session;
    private final int[] hotbarLinks = new int[9];
    private int heldHotbarSlot = -1;
    private AtomicReference<ItemInstance> cursorItem = new AtomicReference<>(null);

    public NukkitPlayerInventory(PlayerSession session) {
        super(NukkitInventoryType.PLAYER);
        this.session = session;
        getObservers().add(session);
        Arrays.fill(hotbarLinks, -1);
    }

    @Override
    public PlayerSession getPlayer() {
        return session;
    }

    @Override
    public int[] getHotbarLinks() {
        return Arrays.copyOf(hotbarLinks, hotbarLinks.length);
    }

    public void setHotbarLink(int slot, int inventorySlot) {
        checkHotbarSlot(slot);
        hotbarLinks[slot] = inventorySlot;
    }

    @Override
    public int getHeldHotbarSlot() {
        int slot = heldHotbarSlot;
        if (slot == -1) {
            return -1;
        }
        return hotbarLinks[slot];
    }

    public Optional<ItemInstance> getHotbarItem(int hotbarSlot) {
        checkHotbarSlot(hotbarSlot);
        int slot = hotbarLinks[hotbarSlot];
        if (slot == -1) {
            return Optional.empty();
        }
        return getItem(hotbarLinks[hotbarSlot]);
    }

    @Override
    public void setHeldHotbarSlot(int slot) {
        setHeldHotbarSlot(slot, true);
    }

    @Override
    public void setHeldHotbarSlot(int slot, boolean sendToPlayer) {
        checkHotbarSlot(slot);
        this.heldHotbarSlot = slot;

        MobEquipmentPacket packet = new MobEquipmentPacket();
        packet.setRuntimeEntityId(session.getEntityId());
        packet.setHotbarSlot((byte) slot);
        packet.setInventorySlot((byte) hotbarLinks[slot]);
        packet.setWindowId(type.getId());
        packet.setItem(getItemInHand().orElse(null));

        if (sendToPlayer) {
            session.getMinecraftSession().addToSendQueue(packet);
        }
        session.getLevel().getPacketManager().queuePacketForViewers(session, packet);
    }

    @Override
    public Optional<ItemInstance> getItemInHand() {
        int slot = getHeldHotbarSlot();
        if (slot == -1) {
            return Optional.empty();
        }
        return getItem(slot);
    }

    public Optional<ItemInstance> getCursorItem() {
        return Optional.ofNullable(cursorItem.get());
    }

    public void setCursorItem(@Nullable ItemInstance item) {
        cursorItem.set(item);
    }

    private void checkHotbarSlot(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < hotbarLinks.length, "Hotbar slot out of range.");
    }
}
