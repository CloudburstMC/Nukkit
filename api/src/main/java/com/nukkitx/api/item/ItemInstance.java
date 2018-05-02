package com.nukkitx.api.item;

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

    default <T extends Metadata> T ensureItemData(Class<T> clazz) {
        return (T) getItemData().get();
    }

    Optional<String> getName();

    List<String> getLore();

    Collection<EnchantmentInstance> getEnchantments();

    ItemInstanceBuilder toBuilder();

    RecipeItemInstanceBuilder toRecipeBuilder();

    boolean isSimilar(@Nonnull ItemInstance itemInstance);

    boolean isMergeable(@Nonnull ItemInstance itemInstance);

    boolean equals(@Nullable ItemInstance item);

    default boolean isFull() {
        return getAmount() >= getItemType().getMaximumStackSize();
    }
}
