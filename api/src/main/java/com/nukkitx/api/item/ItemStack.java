package com.nukkitx.api.item;

import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.metadata.Metadatable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Nonnull
@Immutable
public interface ItemStack extends Metadatable {

    ItemType getItemType();

    int getAmount();

    static boolean isInvalid(@Nullable ItemStack item) {
        return isNull(item) || item.getAmount() <= 0;
    }

    static boolean isNull(@Nullable ItemStack item) {
        return item == null || item.getItemType() == BlockTypes.AIR;
    }

    Optional<String> getName();

    List<String> getLore();

    Collection<EnchantmentInstance> getEnchantments();

    ItemStackBuilder toBuilder();

    RecipeItemStackBuilder toRecipeBuilder();

    boolean isSimilar(@Nonnull ItemStack itemStack);

    boolean isMergeable(@Nonnull ItemStack itemStack);

    boolean equals(@Nullable ItemStack item);

    default boolean isFull() {
        return getAmount() >= getItemType().getMaximumStackSize();
    }

    boolean equals(@Nullable ItemStack other, boolean checkAmount, boolean checkMeta, boolean checkUserData);
}
