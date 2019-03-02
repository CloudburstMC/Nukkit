package com.nukkitx.api.container;

import com.nukkitx.api.Player;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Optional;
import java.util.OptionalInt;

@ParametersAreNullableByDefault
public interface FillingContainer extends Container {

    Optional<Player> getPlayer();

    boolean hasResource(@Nonnull ItemType type);

    boolean canAdd(ItemStack item);

    default int getItemCount(@Nonnull ItemType type) {
        return getItemCount(type, null);
    }

    int getItemCount(@Nonnull ItemType type, Metadata metadata);

    boolean removeResource(@Nonnull ItemType type);

    default int removeResource(ItemStack item) {
        return removeResource(item, true, true, -1);
    }

    int removeResource(ItemStack item, boolean checkMetadata, boolean checkUserdata, int amount);

    default OptionalInt getSlotByItem(ItemStack item) {
        return getSlotByItem(item, true, true);
    }

    OptionalInt getSlotByItem(ItemStack item, boolean checkMetadata, boolean checkUserData);

    void swapItems(int slot1, int slot2);
}
