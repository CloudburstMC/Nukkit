package com.nukkitx.api.item;

import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Nonnull
@Immutable
public interface ItemInstance {

    ItemType getItemType();

    int getAmount();

    Optional<Metadata> getItemData();

    static boolean isNull(@Nullable ItemInstance item) {
        return item == null || item.getItemType() == BlockTypes.AIR || item.getAmount() == 0;
    }

    Optional<String> getName();

    List<String> getLore();

    Collection<EnchantmentInstance> getEnchantments();

    ItemInstanceBuilder toBuilder();

    RecipeItemInstanceBuilder toRecipeBuilder();

    boolean isSimilar(@Nonnull ItemInstance itemInstance);

    boolean isMergeable(@Nonnull ItemInstance itemInstance);

    boolean equals(@Nullable ItemInstance item);

    default <T extends Metadata> T ensureItemData(Class<T> clazz) {
        try {
            return (T) getItemData().orElseThrow(() -> new IllegalArgumentException("ItemInstance has no data"));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }

    default boolean isFull() {
        return getAmount() >= getItemType().getMaximumStackSize();
    }

    boolean equals(@Nullable ItemInstance other, boolean checkAmount, boolean checkMeta, boolean checkUserData);
}
