package com.nukkitx.server.container;

import com.google.common.base.Preconditions;
import com.nukkitx.api.Player;
import com.nukkitx.api.container.FillingContainer;
import com.nukkitx.api.entity.component.PlayerData;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Optional;
import java.util.OptionalInt;

@ParametersAreNullableByDefault
public class NukkitFillingContainer extends NukkitContainer implements FillingContainer {
    private final NukkitPlayerSession session;

    public NukkitFillingContainer(@Nullable NukkitPlayerSession session, int size, ContainerType type) {
        super(type);
        this.session = session;
        setContainerSize(size);
        if (session != null) {
            addContentListener(session);
        }
    }

    public synchronized void swapItems(int slot1, int slot2) {
        checkSlot(slot1);
        checkSlot(slot2);
        ItemStack tempItem = contents[slot1];
        contents[slot1] = contents[slot2];
        contents[slot2] = tempItem;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(session);
    }

    public NukkitPlayerSession getPlayerSession() {
        return session;
    }

    protected boolean isCreative() {
        return session != null && session.ensureAndGet(PlayerData.class).getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public boolean canAdd(ItemStack item) {
        return isCreative() || hasRoomForItem(item);
    }

    public synchronized OptionalInt getSlotByItem(ItemStack item, boolean checkMetadata, boolean checkUserData) {
        if (!ItemStack.isNull(item)) {
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].equals(item, false, checkMetadata, checkUserData)) {
                    return OptionalInt.of(i);
                }
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public synchronized int getItemCount(@Nonnull ItemType type, Metadata metadata) {
        Preconditions.checkNotNull(type, "type");

        int count = 0;

        for (ItemStack item : contents) {
            if (item != null && item.getItemType() == type &&
                    (metadata == null || (item.getMetadata().isPresent() && metadata.equals(item.getMetadata().get())))) {
                count += item.getAmount();
            }
        }

        return count;
    }

    @Override
    public synchronized boolean hasResource(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");

        for (ItemStack item : contents) {
            if (item != null && item.getItemType() == type) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean removeResource(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        boolean removed = false;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getItemType() == type) {
                clearSlot(i);
                removed = true;
            }
        }
        return removed;
    }

    public synchronized int removeResource(ItemStack item, boolean checkMetadata, boolean checkUserdata, int amount) {
        if (ItemStack.isNull(item)) {
            return amount;
        }

        if (amount < 1) {
            amount = item.getAmount();
        }

        for (int i = 0; i < contents.length && amount > 0; i++) {
            ItemStack content = contents[i];
            if (!item.equals(content, false, checkMetadata, checkUserdata)) {
                continue;
            }

            int itemCount = content.getAmount();
            if (itemCount <= amount) {
                amount -= itemCount;
                contents[i] = null;
            } else {
                contents[i] = content.toBuilder().amount(itemCount - amount).build();
                amount = 0;
            }
        }
        return amount;
    }

    @Override
    public int getMaxStackSize() {
        return 254;
    }

    @Override
    public void onOpen(@Nonnull NukkitPlayerSession session) {

    }

    @Override
    public void onClose(@Nonnull NukkitPlayerSession session) {

    }

    public int getHotbarSize() {
        return 9;
    }
}
